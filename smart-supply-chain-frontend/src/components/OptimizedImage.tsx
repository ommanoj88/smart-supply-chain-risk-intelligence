import React, { useState, useRef, useEffect, useCallback } from 'react';

interface OptimizedImageProps {
  src: string;
  alt: string;
  width?: number;
  height?: number;
  className?: string;
  style?: React.CSSProperties;
  placeholder?: string;
  fallback?: string;
  lazy?: boolean;
  quality?: number;
  formats?: ('webp' | 'avif' | 'jpg' | 'png')[];
  sizes?: string;
  priority?: boolean;
  onLoad?: () => void;
  onError?: (error: Event) => void;
}

// Progressive image loading component with format optimization
export const OptimizedImage: React.FC<OptimizedImageProps> = ({
  src,
  alt,
  width,
  height,
  className = '',
  style = {},
  placeholder,
  fallback,
  lazy = true,
  quality = 80,
  formats = ['webp', 'jpg'],
  sizes,
  priority = false,
  onLoad,
  onError,
}) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [isError, setIsError] = useState(false);
  const [currentSrc, setCurrentSrc] = useState<string>(placeholder || '');
  const [isInView, setIsInView] = useState(!lazy || priority);
  const imgRef = useRef<HTMLImageElement>(null);
  const intersectionObserverRef = useRef<IntersectionObserver | null>(null);

  // Generate optimized image URLs for different formats
  const generateImageSources = useCallback((baseSrc: string) => {
    const sources: { srcSet: string; type: string }[] = [];
    
    formats.forEach(format => {
      if (format === 'webp' || format === 'avif') {
        // For modern formats, convert the extension
        const optimizedSrc = baseSrc.replace(/\.(jpg|jpeg|png)$/i, `.${format}`);
        sources.push({
          srcSet: optimizedSrc,
          type: `image/${format}`
        });
      } else {
        // For traditional formats, use as-is or with quality parameter
        let imageSrc = baseSrc;
        if (quality < 100) {
          // Append quality parameter if supported by your CDN/server
          const separator = baseSrc.includes('?') ? '&' : '?';
          imageSrc = `${baseSrc}${separator}q=${quality}`;
        }
        sources.push({
          srcSet: imageSrc,
          type: `image/${format}`
        });
      }
    });

    return sources;
  }, [formats, quality]);

  // Setup intersection observer for lazy loading
  useEffect(() => {
    if (!lazy || priority || isInView) return;

    intersectionObserverRef.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsInView(true);
          intersectionObserverRef.current?.disconnect();
        }
      },
      {
        rootMargin: '50px',
        threshold: 0.1
      }
    );

    if (imgRef.current) {
      intersectionObserverRef.current.observe(imgRef.current);
    }

    return () => {
      intersectionObserverRef.current?.disconnect();
    };
  }, [lazy, priority, isInView]);

  // Load image when it comes into view
  useEffect(() => {
    if (!isInView || isLoaded) return;

    const img = new Image();
    
    img.onload = () => {
      setCurrentSrc(src);
      setIsLoaded(true);
      setIsError(false);
      onLoad?.();
    };

    img.onerror = (error) => {
      setIsError(true);
      if (fallback) {
        setCurrentSrc(fallback);
      }
      onError?.(error);
    };

    img.src = src;
  }, [isInView, src, fallback, isLoaded, onLoad, onError]);

  // Progressive enhancement for responsive images
  const imageSources = generateImageSources(src);

  const imageStyle: React.CSSProperties = {
    ...style,
    width: width ? `${width}px` : style.width,
    height: height ? `${height}px` : style.height,
    opacity: isLoaded ? 1 : 0.6,
    transition: 'opacity 0.3s ease-in-out',
    filter: isLoaded ? 'none' : 'blur(2px)',
  };

  if (!isInView) {
    return (
      <div
        ref={imgRef}
        className={`optimized-image-placeholder ${className}`}
        style={{
          ...imageStyle,
          backgroundColor: '#f0f0f0',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#999',
        }}
      >
        {placeholder ? (
          <img src={placeholder} alt="" style={{ filter: 'blur(2px)' }} />
        ) : (
          <div>Loading...</div>
        )}
      </div>
    );
  }

  return (
    <picture className="optimized-image-container">
      {imageSources.map((source, index) => (
        <source
          key={index}
          srcSet={source.srcSet}
          type={source.type}
          sizes={sizes}
        />
      ))}
      <img
        ref={imgRef}
        src={currentSrc}
        alt={alt}
        className={`optimized-image ${className} ${isLoaded ? 'loaded' : 'loading'} ${isError ? 'error' : ''}`}
        style={imageStyle}
        width={width}
        height={height}
        loading={lazy && !priority ? 'lazy' : 'eager'}
        decoding="async"
      />
    </picture>
  );
};

// Avatar component with fallback initials
interface AvatarProps {
  src?: string;
  name: string;
  size?: number;
  className?: string;
  fallbackColor?: string;
}

export const Avatar: React.FC<AvatarProps> = ({
  src,
  name,
  size = 40,
  className = '',
  fallbackColor = '#007bff',
}) => {
  const [hasError, setHasError] = useState(false);

  const initials = name
    .split(' ')
    .map(word => word.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2);

  const avatarStyle: React.CSSProperties = {
    width: size,
    height: size,
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: fallbackColor,
    color: 'white',
    fontSize: size * 0.4,
    fontWeight: 600,
  };

  if (!src || hasError) {
    return (
      <div className={`avatar avatar-fallback ${className}`} style={avatarStyle}>
        {initials}
      </div>
    );
  }

  return (
    <OptimizedImage
      src={src}
      alt={name}
      width={size}
      height={size}
      className={`avatar ${className}`}
      style={avatarStyle}
      onError={() => setHasError(true)}
      formats={['webp', 'jpg']}
      quality={90}
    />
  );
};

// Image gallery with optimized loading
interface ImageGalleryProps {
  images: Array<{
    src: string;
    alt: string;
    thumbnail?: string;
  }>;
  className?: string;
  lazy?: boolean;
  preloadNext?: number;
}

export const ImageGallery: React.FC<ImageGalleryProps> = ({
  images,
  className = '',
  lazy = true,
  preloadNext = 3,
}) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [preloadedImages] = useState(new Set<string>());

  // Preload next few images
  useEffect(() => {
    if (preloadNext > 0) {
      for (let i = selectedIndex + 1; i <= selectedIndex + preloadNext && i < images.length; i++) {
        const imgSrc = images[i].src;
        if (!preloadedImages.has(imgSrc)) {
          const img = new Image();
          img.src = imgSrc;
          preloadedImages.add(imgSrc);
        }
      }
    }
  }, [selectedIndex, images, preloadNext, preloadedImages]);

  return (
    <div className={`image-gallery ${className}`}>
      <div className="gallery-main">
        <OptimizedImage
          src={images[selectedIndex].src}
          alt={images[selectedIndex].alt}
          lazy={false}
          priority={true}
          className="gallery-main-image"
        />
      </div>
      
      <div className="gallery-thumbnails">
        {images.map((image, index) => (
          <button
            key={index}
            className={`thumbnail-button ${index === selectedIndex ? 'active' : ''}`}
            onClick={() => setSelectedIndex(index)}
          >
            <OptimizedImage
              src={image.thumbnail || image.src}
              alt={image.alt}
              lazy={lazy}
              className="thumbnail-image"
              quality={60}
            />
          </button>
        ))}
      </div>
    </div>
  );
};

// Background image component with optimization
interface BackgroundImageProps {
  src: string;
  children: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
  lazy?: boolean;
  overlay?: boolean;
  overlayColor?: string;
  overlayOpacity?: number;
}

export const BackgroundImage: React.FC<BackgroundImageProps> = ({
  src,
  children,
  className = '',
  style = {},
  lazy = true,
  overlay = false,
  overlayColor = '#000',
  overlayOpacity = 0.3,
}) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [isInView, setIsInView] = useState(!lazy);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!lazy) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsInView(true);
          observer.disconnect();
        }
      },
      { rootMargin: '50px' }
    );

    if (containerRef.current) {
      observer.observe(containerRef.current);
    }

    return () => observer.disconnect();
  }, [lazy]);

  useEffect(() => {
    if (!isInView) return;

    const img = new Image();
    img.onload = () => setIsLoaded(true);
    img.src = src;
  }, [isInView, src]);

  const backgroundStyle: React.CSSProperties = {
    ...style,
    backgroundImage: isLoaded ? `url(${src})` : 'none',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    backgroundRepeat: 'no-repeat',
    position: 'relative',
  };

  return (
    <div
      ref={containerRef}
      className={`background-image ${className} ${isLoaded ? 'loaded' : 'loading'}`}
      style={backgroundStyle}
    >
      {overlay && (
        <div
          className="background-overlay"
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: overlayColor,
            opacity: overlayOpacity,
            pointerEvents: 'none',
          }}
        />
      )}
      <div className="background-content" style={{ position: 'relative', zIndex: 1 }}>
        {children}
      </div>
    </div>
  );
};

export default OptimizedImage;

// CSS for optimized image components
export const optimizedImageStyles = `
.optimized-image-container {
  display: inline-block;
  line-height: 0;
}

.optimized-image {
  max-width: 100%;
  height: auto;
  transition: opacity 0.3s ease-in-out, filter 0.3s ease-in-out;
}

.optimized-image.loading {
  filter: blur(2px);
  opacity: 0.6;
}

.optimized-image.loaded {
  filter: none;
  opacity: 1;
}

.optimized-image.error {
  filter: grayscale(100%);
  opacity: 0.5;
}

.optimized-image-placeholder {
  background-color: #f0f0f0;
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.avatar {
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.avatar-fallback {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.image-gallery {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.gallery-main {
  flex: 1;
  overflow: hidden;
  border-radius: 8px;
}

.gallery-main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.gallery-thumbnails {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
}

.thumbnail-button {
  flex-shrink: 0;
  border: 2px solid transparent;
  border-radius: 4px;
  padding: 2px;
  background: none;
  cursor: pointer;
  transition: border-color 0.2s;
}

.thumbnail-button:hover {
  border-color: #007bff;
}

.thumbnail-button.active {
  border-color: #007bff;
}

.thumbnail-image {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 2px;
}

.background-image {
  min-height: 200px;
  transition: background-image 0.3s ease-in-out;
}

.background-image.loading {
  background-color: #f0f0f0;
}

.background-overlay {
  backdrop-filter: blur(1px);
}
`;