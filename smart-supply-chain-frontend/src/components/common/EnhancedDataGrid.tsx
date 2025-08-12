import React from 'react';
import {
  DataGrid,
  GridColDef,
  GridRowsProp,
  GridToolbar,
  GridActionsCellItem,
  GridRowParams,
  GridPaginationModel,
} from '@mui/x-data-grid';
import {
  Box,
  Paper,
  Chip,
  IconButton,
  Tooltip,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Edit,
  Delete,
  Visibility,
  Download,
  FilterList,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

interface EnhancedDataGridProps {
  rows: GridRowsProp;
  columns: GridColDef[];
  loading?: boolean;
  title?: string;
  height?: number;
  pageSize?: number;
  pageSizeOptions?: number[];
  onRowClick?: (params: GridRowParams) => void;
  onEdit?: (id: string | number) => void;
  onDelete?: (id: string | number) => void;
  onView?: (id: string | number) => void;
  onExport?: () => void;
  toolbar?: boolean;
  checkboxSelection?: boolean;
  disableSelectionOnClick?: boolean;
  density?: 'compact' | 'standard' | 'comfortable';
  onPaginationModelChange?: (model: GridPaginationModel) => void;
  paginationModel?: GridPaginationModel;
  rowCount?: number;
  paginationMode?: 'client' | 'server';
}

/**
 * Enhanced DataGrid component with enterprise features
 */
export const EnhancedDataGrid: React.FC<EnhancedDataGridProps> = ({
  rows,
  columns,
  loading = false,
  title,
  height = 600,
  pageSize = 25,
  pageSizeOptions = [10, 25, 50, 100],
  onRowClick,
  onEdit,
  onDelete,
  onView,
  onExport,
  toolbar = true,
  checkboxSelection = false,
  disableSelectionOnClick = true,
  density = 'standard',
  onPaginationModelChange,
  paginationModel,
  rowCount,
  paginationMode = 'client',
}) => {
  const theme = useTheme();

  // Add action column if any action handlers are provided
  const enhancedColumns: GridColDef[] = React.useMemo(() => {
    const hasActions = onEdit || onDelete || onView;
    
    if (!hasActions) return columns;

    const actionColumn: GridColDef = {
      field: 'actions',
      type: 'actions',
      headerName: 'Actions',
      width: 120,
      cellClassName: 'actions',
      getActions: ({ id }) => {
        const actions = [];

        if (onView) {
          actions.push(
            <GridActionsCellItem
              icon={
                <Tooltip title="View Details">
                  <Visibility />
                </Tooltip>
              }
              label="View"
              onClick={() => onView(id)}
              color="inherit"
            />
          );
        }

        if (onEdit) {
          actions.push(
            <GridActionsCellItem
              icon={
                <Tooltip title="Edit">
                  <Edit />
                </Tooltip>
              }
              label="Edit"
              onClick={() => onEdit(id)}
              color="inherit"
            />
          );
        }

        if (onDelete) {
          actions.push(
            <GridActionsCellItem
              icon={
                <Tooltip title="Delete">
                  <Delete />
                </Tooltip>
              }
              label="Delete"
              onClick={() => onDelete(id)}
              color="inherit"
            />
          );
        }

        return actions;
      },
    };

    return [...columns, actionColumn];
  }, [columns, onEdit, onDelete, onView]);

  const CustomToolbar = () => (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        p: 2,
        borderBottom: `1px solid ${theme.palette.divider}`,
      }}
    >
      <Box>
        {title && (
          <Typography variant="h6" component="h2" fontWeight={600}>
            {title}
          </Typography>
        )}
      </Box>
      <Box display="flex" gap={1}>
        {onExport && (
          <Tooltip title="Export Data">
            <IconButton onClick={onExport} size="small">
              <Download />
            </IconButton>
          </Tooltip>
        )}
        <GridToolbar />
      </Box>
    </Box>
  );

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Paper
        sx={{
          height: height + (title ? 60 : 0),
          width: '100%',
          '& .MuiDataGrid-root': {
            border: 'none',
          },
          '& .MuiDataGrid-cell': {
            borderBottom: `1px solid ${theme.palette.divider}`,
          },
          '& .MuiDataGrid-columnHeaders': {
            backgroundColor: theme.palette.grey[50],
            borderBottom: `2px solid ${theme.palette.primary.main}`,
          },
          '& .MuiDataGrid-virtualScroller': {
            backgroundColor: theme.palette.background.paper,
          },
          '& .MuiDataGrid-footerContainer': {
            borderTop: `1px solid ${theme.palette.divider}`,
            backgroundColor: theme.palette.grey[50],
          },
          '& .MuiCheckbox-root svg': {
            width: 16,
            height: 16,
            backgroundColor: 'transparent',
            border: `1px solid ${theme.palette.divider}`,
            borderRadius: 2,
          },
          '& .MuiCheckbox-root svg path': {
            display: 'none',
          },
          '& .MuiCheckbox-root.Mui-checked:not(.MuiCheckbox-indeterminate) svg': {
            backgroundColor: theme.palette.primary.main,
            borderColor: theme.palette.primary.main,
          },
          '& .MuiCheckbox-root.Mui-checked .MuiIconButton-label:after': {
            position: 'absolute',
            display: 'table',
            border: '2px solid #fff',
            borderTop: 0,
            borderLeft: 0,
            transform: 'rotate(45deg) translate(-50%,-50%)',
            opacity: 1,
            transition: 'all .2s cubic-bezier(.12,.4,.29,1.46) .1s',
            content: '""',
            top: '50%',
            left: '39%',
            width: 5.71428571,
            height: 9.14285714,
          },
          '& .MuiDataGrid-row:hover': {
            backgroundColor: theme.palette.action.hover,
          },
        }}
      >
        <DataGrid
          rows={rows}
          columns={enhancedColumns}
          loading={loading}
          onRowClick={onRowClick}
          slots={{
            toolbar: toolbar ? CustomToolbar : undefined,
          }}
          slotProps={{
            toolbar: {
              showQuickFilter: true,
              quickFilterProps: { debounceMs: 500 },
            },
          }}
          checkboxSelection={checkboxSelection}
          disableRowSelectionOnClick={disableSelectionOnClick}
          density={density}
          pageSizeOptions={pageSizeOptions}
          paginationModel={paginationModel || { page: 0, pageSize }}
          onPaginationModelChange={onPaginationModelChange}
          rowCount={rowCount}
          paginationMode={paginationMode}
          sx={{
            height: height,
            '& .MuiDataGrid-cell:focus': {
              outline: 'none',
            },
            '& .MuiDataGrid-row:hover': {
              cursor: onRowClick ? 'pointer' : 'default',
            },
          }}
        />
      </Paper>
    </motion.div>
  );
};

// Helper function to create status chip column
export const createStatusColumn = (field: string, headerName: string = 'Status'): GridColDef => ({
  field,
  headerName,
  width: 120,
  renderCell: (params) => {
    const getStatusColor = (status: string) => {
      switch (status.toLowerCase()) {
        case 'active': return 'success';
        case 'inactive': return 'default';
        case 'pending': return 'warning';
        case 'error': return 'error';
        case 'high': return 'error';
        case 'medium': return 'warning';
        case 'low': return 'success';
        default: return 'default';
      }
    };

    return (
      <Chip
        label={params.value}
        color={getStatusColor(params.value)}
        size="small"
        variant="outlined"
      />
    );
  },
});

// Helper function to create date column
export const createDateColumn = (field: string, headerName: string): GridColDef => ({
  field,
  headerName,
  width: 150,
  valueFormatter: (params) => {
    if (!params.value) return '';
    return new Date(params.value).toLocaleDateString();
  },
});

export default EnhancedDataGrid;