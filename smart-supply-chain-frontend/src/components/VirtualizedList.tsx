import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';

interface VirtualizedListProps<T> {
  items: T[];
  itemHeight: number;
  containerHeight: number;
  renderItem: (item: T, index: number) => React.ReactNode;
  overscan?: number;
  onLoadMore?: () => void;
  hasMore?: boolean;
  loading?: boolean;
  className?: string;
  itemKey?: (item: T, index: number) => string | number;
}

// High-performance virtualized list component
export const VirtualizedList = <T,>({
  items,
  itemHeight,
  containerHeight,
  renderItem,
  overscan = 5,
  onLoadMore,
  hasMore = false,
  loading = false,
  className = '',
  itemKey = (_, index) => index,
}: VirtualizedListProps<T>) => {
  const [scrollTop, setScrollTop] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);
  const scrollElementRef = useRef<HTMLDivElement>(null);

  // Calculate visible range
  const visibleRange = useMemo(() => {
    const startIndex = Math.max(0, Math.floor(scrollTop / itemHeight) - overscan);
    const endIndex = Math.min(
      items.length - 1,
      Math.floor((scrollTop + containerHeight) / itemHeight) + overscan
    );
    return { startIndex, endIndex };
  }, [scrollTop, itemHeight, containerHeight, items.length, overscan]);

  // Handle scroll events with throttling
  const handleScroll = useCallback(
    throttle((e: React.UIEvent<HTMLDivElement>) => {
      const scrollTop = e.currentTarget.scrollTop;
      setScrollTop(scrollTop);

      // Trigger load more when near the bottom
      if (
        onLoadMore &&
        hasMore &&
        !loading &&
        scrollTop + containerHeight >= items.length * itemHeight - 200
      ) {
        onLoadMore();
      }
    }, 16), // ~60fps
    [itemHeight, containerHeight, items.length, onLoadMore, hasMore, loading]
  );

  // Visible items
  const visibleItems = useMemo(() => {
    const result = [];
    for (let i = visibleRange.startIndex; i <= visibleRange.endIndex; i++) {
      if (items[i]) {
        result.push({
          item: items[i],
          index: i,
          key: itemKey(items[i], i),
        });
      }
    }
    return result;
  }, [items, visibleRange, itemKey]);

  return (
    <div
      ref={containerRef}
      className={`virtualized-list ${className}`}
      style={{
        height: containerHeight,
        overflow: 'auto',
        position: 'relative',
      }}
      onScroll={handleScroll}
    >
      <div
        ref={scrollElementRef}
        style={{
          height: items.length * itemHeight,
          position: 'relative',
        }}
      >
        {visibleItems.map(({ item, index, key }) => (
          <div
            key={key}
            style={{
              position: 'absolute',
              top: index * itemHeight,
              left: 0,
              right: 0,
              height: itemHeight,
            }}
            className="virtualized-list-item"
          >
            {renderItem(item, index)}
          </div>
        ))}
        
        {loading && (
          <div
            style={{
              position: 'absolute',
              top: items.length * itemHeight,
              left: 0,
              right: 0,
              height: 50,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <div className="loading-indicator">Loading more...</div>
          </div>
        )}
      </div>
    </div>
  );
};

// Grid virtualization for tabular data
interface VirtualizedGridProps<T> {
  items: T[];
  rowHeight: number;
  columnWidths: number[];
  containerWidth: number;
  containerHeight: number;
  renderCell: (item: T, columnIndex: number, rowIndex: number) => React.ReactNode;
  renderHeader?: (columnIndex: number) => React.ReactNode;
  overscan?: number;
  onLoadMore?: () => void;
  hasMore?: boolean;
  loading?: boolean;
}

export const VirtualizedGrid = <T,>({
  items,
  rowHeight,
  columnWidths,
  containerWidth,
  containerHeight,
  renderCell,
  renderHeader,
  overscan = 5,
  onLoadMore,
  hasMore = false,
  loading = false,
}: VirtualizedGridProps<T>) => {
  const [scrollTop, setScrollTop] = useState(0);
  const [scrollLeft, setScrollLeft] = useState(0);

  const totalWidth = columnWidths.reduce((sum, width) => sum + width, 0);
  const headerHeight = renderHeader ? rowHeight : 0;

  const visibleRowRange = useMemo(() => {
    const startRow = Math.max(0, Math.floor(scrollTop / rowHeight) - overscan);
    const endRow = Math.min(
      items.length - 1,
      Math.floor((scrollTop + containerHeight - headerHeight) / rowHeight) + overscan
    );
    return { startRow, endRow };
  }, [scrollTop, rowHeight, containerHeight, headerHeight, items.length, overscan]);

  const visibleColumnRange = useMemo(() => {
    let startCol = 0;
    let currentX = 0;
    
    // Find first visible column
    for (let i = 0; i < columnWidths.length; i++) {
      if (currentX + columnWidths[i] > scrollLeft) {
        startCol = Math.max(0, i - overscan);
        break;
      }
      currentX += columnWidths[i];
    }

    // Find last visible column
    let endCol = columnWidths.length - 1;
    currentX = 0;
    for (let i = 0; i < columnWidths.length; i++) {
      currentX += columnWidths[i];
      if (currentX > scrollLeft + containerWidth) {
        endCol = Math.min(columnWidths.length - 1, i + overscan);
        break;
      }
    }

    return { startCol, endCol };
  }, [scrollLeft, containerWidth, columnWidths, overscan]);

  const handleScroll = useCallback(
    throttle((e: React.UIEvent<HTMLDivElement>) => {
      const { scrollTop, scrollLeft } = e.currentTarget;
      setScrollTop(scrollTop);
      setScrollLeft(scrollLeft);

      // Load more when near bottom
      if (
        onLoadMore &&
        hasMore &&
        !loading &&
        scrollTop + containerHeight >= items.length * rowHeight + headerHeight - 100
      ) {
        onLoadMore();
      }
    }, 16),
    [containerHeight, rowHeight, headerHeight, items.length, onLoadMore, hasMore, loading]
  );

  const getColumnOffset = (columnIndex: number) => {
    return columnWidths.slice(0, columnIndex).reduce((sum, width) => sum + width, 0);
  };

  return (
    <div
      className="virtualized-grid"
      style={{
        width: containerWidth,
        height: containerHeight,
        overflow: 'auto',
        position: 'relative',
      }}
      onScroll={handleScroll}
    >
      <div
        style={{
          width: totalWidth,
          height: items.length * rowHeight + headerHeight,
          position: 'relative',
        }}
      >
        {/* Header */}
        {renderHeader && (
          <div
            style={{
              position: 'sticky',
              top: 0,
              left: 0,
              right: 0,
              height: headerHeight,
              backgroundColor: 'white',
              zIndex: 1,
              borderBottom: '1px solid #e0e0e0',
            }}
          >
            {Array.from({ length: visibleColumnRange.endCol - visibleColumnRange.startCol + 1 }).map((_, i) => {
              const colIndex = visibleColumnRange.startCol + i;
              return (
                <div
                  key={colIndex}
                  style={{
                    position: 'absolute',
                    left: getColumnOffset(colIndex),
                    top: 0,
                    width: columnWidths[colIndex],
                    height: headerHeight,
                    borderRight: '1px solid #e0e0e0',
                  }}
                  className="grid-header-cell"
                >
                  {renderHeader(colIndex)}
                </div>
              );
            })}
          </div>
        )}

        {/* Rows */}
        {Array.from({ length: visibleRowRange.endRow - visibleRowRange.startRow + 1 }).map((_, i) => {
          const rowIndex = visibleRowRange.startRow + i;
          const item = items[rowIndex];
          if (!item) return null;

          return (
            <div
              key={rowIndex}
              style={{
                position: 'absolute',
                top: rowIndex * rowHeight + headerHeight,
                left: 0,
                right: 0,
                height: rowHeight,
              }}
              className="grid-row"
            >
              {Array.from({ length: visibleColumnRange.endCol - visibleColumnRange.startCol + 1 }).map((_, j) => {
                const colIndex = visibleColumnRange.startCol + j;
                return (
                  <div
                    key={colIndex}
                    style={{
                      position: 'absolute',
                      left: getColumnOffset(colIndex),
                      top: 0,
                      width: columnWidths[colIndex],
                      height: rowHeight,
                      borderRight: '1px solid #e0e0e0',
                      borderBottom: '1px solid #e0e0e0',
                    }}
                    className="grid-cell"
                  >
                    {renderCell(item, colIndex, rowIndex)}
                  </div>
                );
              })}
            </div>
          );
        })}

        {loading && (
          <div
            style={{
              position: 'absolute',
              top: items.length * rowHeight + headerHeight,
              left: 0,
              right: 0,
              height: 50,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <div className="loading-indicator">Loading more...</div>
          </div>
        )}
      </div>
    </div>
  );
};

// Utility function for throttling
function throttle<T extends (...args: any[]) => any>(func: T, limit: number): T {
  let inThrottle: boolean;
  return function (this: any, ...args: any[]) {
    if (!inThrottle) {
      func.apply(this, args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  } as T;
}

// Hook for infinite scrolling
export const useInfiniteScroll = <T,>(
  fetchMore: () => Promise<T[]>,
  hasMore: boolean,
  threshold: number = 100
) => {
  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadMore = useCallback(async () => {
    if (loading || !hasMore) return;

    setLoading(true);
    setError(null);

    try {
      const newItems = await fetchMore();
      setItems(prev => [...prev, ...newItems]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load more items');
    } finally {
      setLoading(false);
    }
  }, [fetchMore, hasMore, loading]);

  return { items, setItems, loading, error, loadMore };
};

export default VirtualizedList;

// CSS for virtualized components
export const virtualizedStyles = `
.virtualized-list {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.virtualized-list-item {
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
}

.virtualized-list-item:hover {
  background-color: #f8f9fa;
}

.virtualized-grid {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.grid-header-cell {
  padding: 8px 12px;
  background-color: #f8f9fa;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.grid-cell {
  padding: 8px 12px;
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.grid-row:nth-child(even) {
  background-color: #fafafa;
}

.loading-indicator {
  padding: 10px;
  text-align: center;
  color: #666;
  font-style: italic;
}
`;