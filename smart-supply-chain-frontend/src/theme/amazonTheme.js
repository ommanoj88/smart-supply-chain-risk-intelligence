// Amazon-inspired design tokens
export const amazonTheme = {
  // Colors (Amazon color palette)
  colors: {
    primary: {
      50: '#fff8f1',
      100: '#ffead3',
      200: '#ffd3a5',
      300: '#ffb366',
      400: '#ff9900', // Amazon Orange
      500: '#e67e00',
      600: '#cc6600',
      700: '#b35200',
      800: '#993d00',
      900: '#7a2e00',
    },
    navy: {
      50: '#f7f8fa',
      100: '#ebeff4',
      200: '#d3dce6',
      300: '#adc0d1',
      400: '#809fb8',
      500: '#5f7f9e',
      600: '#4a6584',
      700: '#3d536b',
      800: '#344658',
      900: '#232f3e', // Amazon Navy
    },
    success: {
      50: '#ecfdf5',
      100: '#d1fae5',
      200: '#a7f3d0',
      300: '#6ee7b7',
      400: '#34d399',
      500: '#067d62', // Amazon Green
      600: '#047857',
      700: '#065f46',
      800: '#064e3b',
      900: '#022c22',
    },
    warning: {
      50: '#fffbeb',
      100: '#fef3c7',
      200: '#fde68a',
      300: '#fcd34d',
      400: '#f79400', // Orange Warning
      500: '#d97706',
      600: '#b45309',
      700: '#92400e',
      800: '#78350f',
      900: '#451a03',
    },
    error: {
      50: '#fef2f2',
      100: '#fee2e2',
      200: '#fecaca',
      300: '#fca5a5',
      400: '#f87171',
      500: '#cc0c39', // Amazon Red
      600: '#dc2626',
      700: '#b91c1c',
      800: '#991b1b',
      900: '#7f1d1d',
    },
    gray: {
      50: '#f9fafb',
      100: '#f3f4f6',
      200: '#e5e7eb',
      300: '#d1d5db',
      400: '#9ca3af',
      500: '#6b7280',
      600: '#4b5563',
      700: '#374151',
      800: '#1f2937',
      900: '#111827',
    },
    background: {
      primary: '#ffffff',
      secondary: '#f8f9fa',
      tertiary: '#f1f3f4',
    }
  },

  // Typography (Amazon Ember font family)
  typography: {
    fontFamily: {
      primary: '"Amazon Ember", "Helvetica Neue", Helvetica, Arial, sans-serif',
      mono: '"SF Mono", "Monaco", "Inconsolata", "Roboto Mono", monospace'
    },
    fontSize: {
      xs: '0.75rem',     // 12px
      sm: '0.875rem',    // 14px
      base: '1rem',      // 16px
      lg: '1.125rem',    // 18px
      xl: '1.25rem',     // 20px
      '2xl': '1.5rem',   // 24px
      '3xl': '1.875rem', // 30px
      '4xl': '2.25rem',  // 36px
      '5xl': '3rem',     // 48px
    },
    fontWeight: {
      normal: '400',
      medium: '500',
      semibold: '600',
      bold: '700',
    },
    lineHeight: {
      tight: '1.25',
      normal: '1.5',
      relaxed: '1.75',
    }
  },

  // Spacing (4px base unit - Amazon's system)
  spacing: {
    0: '0',
    1: '0.25rem',  // 4px
    2: '0.5rem',   // 8px
    3: '0.75rem',  // 12px
    4: '1rem',     // 16px
    5: '1.25rem',  // 20px
    6: '1.5rem',   // 24px
    8: '2rem',     // 32px
    10: '2.5rem',  // 40px
    12: '3rem',    // 48px
    16: '4rem',    // 64px
    20: '5rem',    // 80px
    24: '6rem',    // 96px
  },

  // Elevation (Amazon-style shadows)
  elevation: {
    none: 'none',
    sm: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
    base: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
    md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
    lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
  },

  // Border radius
  borderRadius: {
    none: '0',
    sm: '0.125rem',  // 2px
    base: '0.25rem', // 4px
    md: '0.375rem',  // 6px
    lg: '0.5rem',    // 8px
    xl: '0.75rem',   // 12px
    '2xl': '1rem',   // 16px
    full: '9999px',
  },

  // Transitions
  transitions: {
    default: 'all 0.15s ease-in-out',
    fast: 'all 0.1s ease-in-out',
    slow: 'all 0.3s ease-in-out',
  },

  // Z-index scale
  zIndex: {
    hide: -1,
    auto: 'auto',
    base: 0,
    docked: 10,
    dropdown: 1000,
    sticky: 1100,
    banner: 1200,
    overlay: 1300,
    modal: 1400,
    popover: 1500,
    skipLink: 1600,
    toast: 1700,
    tooltip: 1800,
  }
};

// Amazon-inspired component variants
export const componentVariants = {
  // Button variants
  button: {
    primary: {
      bg: 'amazonTheme.colors.primary[400]',
      color: 'white',
      hover: 'amazonTheme.colors.primary[500]',
      active: 'amazonTheme.colors.primary[600]',
    },
    secondary: {
      bg: 'amazonTheme.colors.gray[100]',
      color: 'amazonTheme.colors.gray[900]',
      hover: 'amazonTheme.colors.gray[200]',
      active: 'amazonTheme.colors.gray[300]',
    },
    navy: {
      bg: 'amazonTheme.colors.navy[900]',
      color: 'white',
      hover: 'amazonTheme.colors.navy[800]',
      active: 'amazonTheme.colors.navy[700]',
    }
  },

  // Card variants
  card: {
    elevated: {
      bg: 'amazonTheme.colors.background.primary',
      shadow: 'amazonTheme.elevation.lg',
      border: 'none',
    },
    outlined: {
      bg: 'amazonTheme.colors.background.primary',
      shadow: 'amazonTheme.elevation.sm',
      border: `1px solid ${amazonTheme.colors.gray[200]}`,
    },
    flat: {
      bg: 'amazonTheme.colors.background.secondary',
      shadow: 'none',
      border: 'none',
    }
  },

  // Status indicators
  status: {
    success: {
      bg: 'amazonTheme.colors.success[50]',
      color: 'amazonTheme.colors.success[700]',
      border: 'amazonTheme.colors.success[200]',
    },
    warning: {
      bg: 'amazonTheme.colors.warning[50]',
      color: 'amazonTheme.colors.warning[700]',
      border: 'amazonTheme.colors.warning[200]',
    },
    error: {
      bg: 'amazonTheme.colors.error[50]',
      color: 'amazonTheme.colors.error[700]',
      border: 'amazonTheme.colors.error[200]',
    },
    info: {
      bg: 'amazonTheme.colors.navy[50]',
      color: 'amazonTheme.colors.navy[700]',
      border: 'amazonTheme.colors.navy[200]',
    }
  }
};

export default amazonTheme;