import { createTheme, ThemeOptions, PaletteOptions } from '@mui/material/styles';
import { alpha } from '@mui/material/styles';

// Premium Enterprise Design Tokens - Natural & Sophisticated
const tokens = {
  // Deep Navy Primary - Sophisticated enterprise blue
  primary: {
    50: '#f0f9ff',
    100: '#e0f2fe',
    200: '#bae6fd',
    300: '#7dd3fc',
    400: '#38bdf8',
    500: '#0ea5e9',
    600: '#0284c7',
    700: '#0369a1',
    800: '#075985',
    900: '#1e3a8a', // Deep navy primary
  },
  
  // Secondary colors
  secondary: {
    50: '#f3e5f5',
    100: '#e1bee7',
    200: '#ce93d8',
    300: '#ba68c8',
    400: '#ab47bc',
    500: '#9c27b0',
    600: '#8e24aa',
    700: '#7b1fa2',
    800: '#6a1b9a',
    900: '#4a148c',
  },
  
  // Crimson Error - Sophisticated red
  error: {
    50: '#fef2f2',
    100: '#fee2e2',
    200: '#fecaca',
    300: '#fca5a5',
    400: '#f87171',
    500: '#ef4444',
    600: '#dc2626', // Crimson primary
    700: '#b91c1c',
    800: '#991b1b',
    900: '#7f1d1d',
  },
  
  // Amber Warning - Premium warm amber
  warning: {
    50: '#fffbeb',
    100: '#fef3c7',
    200: '#fde68a',
    300: '#fcd34d',
    400: '#fbbf24',
    500: '#f59e0b', // Amber primary
    600: '#d97706',
    700: '#b45309',
    800: '#92400e',
    900: '#78350f',
  },
  
  // Emerald Success - Forest green with emerald accents
  success: {
    50: '#ecfdf5',
    100: '#d1fae5',
    200: '#a7f3d0',
    300: '#6ee7b7',
    400: '#34d399',
    500: '#10b981',
    600: '#059669', // Forest green primary
    700: '#047857',
    800: '#065f46',
    900: '#064e3b',
  },
  
  // Info colors
  info: {
    50: '#e1f5fe',
    100: '#b3e5fc',
    200: '#81d4fa',
    300: '#4fc3f7',
    400: '#29b6f6',
    500: '#03a9f4',
    600: '#039be5',
    700: '#0288d1',
    800: '#0277bd',
    900: '#01579b',
  },
  
  // Warm Neutrals - Sophisticated gray system with subtle undertones
  grey: {
    50: '#fafafa',
    100: '#f4f4f5',
    200: '#e4e4e7',
    300: '#d4d4d8',
    400: '#a1a1aa',
    500: '#71717a',
    600: '#52525b',
    700: '#3f3f46',
    800: '#27272a',
    900: '#18181b',
  },
  
  // Bright accent blue for highlights and interactions  
  accent: {
    50: '#eff6ff',
    100: '#dbeafe', 
    200: '#bfdbfe',
    300: '#93c5fd',
    400: '#60a5fa',
    500: '#3b82f6', // Bright blue primary
    600: '#2563eb',
    700: '#1d4ed8',
    800: '#1e40af',
    900: '#1e3a8a',
  },
};

// Premium Typography Scale - Human-centered typography
const typography = {
  fontFamily: [
    'Inter',
    '-apple-system',
    'BlinkMacSystemFont',
    'Segoe UI',
    'Roboto',
    'Oxygen',
    'Ubuntu',
    'Cantarell',
    'Fira Sans',
    'Droid Sans',
    'Helvetica Neue',
    'sans-serif',
  ].join(','),
  h1: {
    fontSize: '3rem', // 48px
    fontWeight: 700,
    lineHeight: 1.1,
    letterSpacing: '-0.025em',
    fontFeatureSettings: '"cv02","cv03","cv04","cv11"',
  },
  h2: {
    fontSize: '2.25rem', // 36px
    fontWeight: 600,
    lineHeight: 1.2,
    letterSpacing: '-0.025em',
    fontFeatureSettings: '"cv02","cv03","cv04","cv11"',
  },
  h3: {
    fontSize: '1.875rem', // 30px
    fontWeight: 600,
    lineHeight: 1.25,
    letterSpacing: '-0.02em',
    fontFeatureSettings: '"cv02","cv03","cv04","cv11"',
  },
  h4: {
    fontSize: '1.5rem', // 24px
    fontWeight: 600,
    lineHeight: 1.3,
    letterSpacing: '-0.01em',
    fontFeatureSettings: '"cv02","cv03","cv04","cv11"',
  },
  h5: {
    fontSize: '1.25rem', // 20px
    fontWeight: 600,
    lineHeight: 1.4,
    letterSpacing: '-0.01em',
    fontFeatureSettings: '"cv02","cv03","cv04","cv11"',
  },
  h6: {
    fontSize: '1.125rem', // 18px
    fontWeight: 600,
    lineHeight: 1.4,
    letterSpacing: '-0.005em',
    fontFeatureSettings: '"cv02","cv03","cv04","cv11"',
  },
  subtitle1: {
    fontSize: '1rem', // 16px
    fontWeight: 500,
    lineHeight: 1.5,
    letterSpacing: '0em',
  },
  subtitle2: {
    fontSize: '0.875rem', // 14px
    fontWeight: 500,
    lineHeight: 1.5,
    letterSpacing: '0.025em',
  },
  body1: {
    fontSize: '1rem', // 16px
    fontWeight: 400,
    lineHeight: 1.6,
    letterSpacing: '0em',
  },
  body2: {
    fontSize: '0.875rem', // 14px
    fontWeight: 400,
    lineHeight: 1.6,
    letterSpacing: '0.025em',
  },
  caption: {
    fontSize: '0.75rem', // 12px
    fontWeight: 400,
    lineHeight: 1.4,
    letterSpacing: '0.05em',
  },
  overline: {
    fontSize: '0.75rem', // 12px
    fontWeight: 600,
    lineHeight: 1.4,
    letterSpacing: '0.1em',
    textTransform: 'uppercase' as const,
  },
  button: {
    fontWeight: 500,
    letterSpacing: '0.025em',
    textTransform: 'none' as const,
  },
};

// Light theme configuration - Premium Enterprise Look
const lightPalette: PaletteOptions = {
  mode: 'light',
  primary: {
    main: tokens.primary[900], // Deep navy for sophistication
    light: tokens.primary[500],
    dark: tokens.primary[900],
    contrastText: '#ffffff',
  },
  secondary: {
    main: tokens.accent[500], // Bright blue for accents
    light: tokens.accent[300],
    dark: tokens.accent[700],
    contrastText: '#ffffff',
  },
  error: {
    main: tokens.error[600], // Crimson
    light: tokens.error[400],
    dark: tokens.error[800],
    contrastText: '#ffffff',
  },
  warning: {
    main: tokens.warning[500], // Amber
    light: tokens.warning[300],
    dark: tokens.warning[700],
    contrastText: 'rgba(0, 0, 0, 0.87)',
  },
  info: {
    main: tokens.accent[500], // Bright blue
    light: tokens.accent[300],
    dark: tokens.accent[700],
    contrastText: '#ffffff',
  },
  success: {
    main: tokens.success[600], // Forest green
    light: tokens.success[400],
    dark: tokens.success[800],
    contrastText: '#ffffff',
  },
  grey: tokens.grey,
  background: {
    default: '#fafafa', // Warm light background
    paper: '#ffffff',
  },
  text: {
    primary: tokens.grey[900], // Rich black for text
    secondary: tokens.grey[600], // Warm gray for secondary text
    disabled: tokens.grey[400],
  },
  divider: alpha(tokens.grey[900], 0.08), // Subtle dividers
  action: {
    active: tokens.grey[700],
    hover: alpha(tokens.grey[900], 0.04),
    selected: alpha(tokens.primary[900], 0.08),
    disabled: tokens.grey[400],
    disabledBackground: alpha(tokens.grey[900], 0.06),
  },
};

// Dark theme configuration - Premium Dark Mode
const darkPalette: PaletteOptions = {
  mode: 'dark',
  primary: {
    main: tokens.accent[400], // Bright blue for dark mode
    light: tokens.accent[300],
    dark: tokens.accent[600],
    contrastText: '#000000',
  },
  secondary: {
    main: tokens.success[400], // Emerald accents
    light: tokens.success[300],
    dark: tokens.success[600],
    contrastText: '#000000',
  },
  error: {
    main: tokens.error[400],
    light: tokens.error[300],
    dark: tokens.error[600],
    contrastText: '#000000',
  },
  warning: {
    main: tokens.warning[400],
    light: tokens.warning[300],
    dark: tokens.warning[600],
    contrastText: '#000000',
  },
  info: {
    main: tokens.accent[400],
    light: tokens.accent[300],
    dark: tokens.accent[600],
    contrastText: '#000000',
  },
  success: {
    main: tokens.success[400],
    light: tokens.success[300],
    dark: tokens.success[600],
    contrastText: '#000000',
  },
  grey: tokens.grey,
  background: {
    default: '#0f0f0f', // Rich dark background
    paper: '#1a1a1a', // Elevated dark surfaces
  },
  text: {
    primary: '#ffffff',
    secondary: alpha('#ffffff', 0.75),
    disabled: alpha('#ffffff', 0.4),
  },
  divider: alpha('#ffffff', 0.08),
  action: {
    active: '#ffffff',
    hover: alpha('#ffffff', 0.08),
    selected: alpha(tokens.accent[400], 0.16),
    disabled: alpha('#ffffff', 0.3),
    disabledBackground: alpha('#ffffff', 0.08),
  },
};

// Premium Enterprise Theme Configuration
const commonThemeOptions: ThemeOptions = {
  typography: typography as any,
  shape: {
    borderRadius: 16, // More modern, softer borders
  },
  spacing: 8, // Golden ratio-based spacing
  components: {
    // Premium Button Styling
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          textTransform: 'none',
          fontWeight: 500,
          fontSize: '0.875rem',
          padding: '10px 24px',
          boxShadow: 'none',
          transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            boxShadow: 'none',
            transform: 'translateY(-1px)',
          },
          '&:active': {
            transform: 'translateY(0)',
          },
        },
        contained: {
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
          '&:hover': {
            boxShadow: '0 4px 16px rgba(0, 0, 0, 0.15)',
            transform: 'translateY(-2px)',
          },
        },
        outlined: {
          borderWidth: '1.5px',
          '&:hover': {
            borderWidth: '1.5px',
            backgroundColor: alpha(tokens.primary[900], 0.04),
          },
        },
      },
    },
    
    // Sophisticated Card Design
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 20,
          border: `1px solid ${alpha(tokens.grey[900], 0.08)}`,
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05), 0 4px 16px rgba(0, 0, 0, 0.04)',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08), 0 16px 32px rgba(0, 0, 0, 0.06)',
            transform: 'translateY(-2px)',
          },
        },
      },
    },
    
    // Modern Paper Elevations
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          border: `1px solid ${alpha(tokens.grey[900], 0.06)}`,
        },
        elevation1: {
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05), 0 2px 8px rgba(0, 0, 0, 0.04)',
        },
        elevation2: {
          boxShadow: '0 2px 6px rgba(0, 0, 0, 0.06), 0 8px 24px rgba(0, 0, 0, 0.05)',
        },
        elevation3: {
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08), 0 16px 32px rgba(0, 0, 0, 0.06)',
        },
      },
    },
    
    // Elegant Chips
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          fontWeight: 500,
          fontSize: '0.875rem',
          height: 32,
          '& .MuiChip-label': {
            paddingLeft: 12,
            paddingRight: 12,
          },
        },
      },
    },
    
    // Modern Input Fields
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 12,
            transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            '&:hover': {
              '& .MuiOutlinedInput-notchedOutline': {
                borderColor: alpha(tokens.primary[900], 0.4),
              },
            },
            '&.Mui-focused': {
              '& .MuiOutlinedInput-notchedOutline': {
                borderWidth: 2,
                borderColor: tokens.primary[900],
              },
            },
          },
        },
      },
    },
    
    // Professional Table Headers
    MuiTableHead: {
      styleOverrides: {
        root: {
          '& .MuiTableCell-head': {
            fontWeight: 600,
            textTransform: 'uppercase',
            fontSize: '0.75rem',
            letterSpacing: '0.1em',
            color: tokens.grey[600],
            backgroundColor: alpha(tokens.grey[100], 0.5),
          },
        },
      },
    },
    
    // Enhanced AppBar
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04)',
          borderBottom: `1px solid ${alpha(tokens.grey[900], 0.08)}`,
        },
      },
    },
  },
};

export const createAppTheme = (mode: 'light' | 'dark') => {
  const palette = mode === 'light' ? lightPalette : darkPalette;
  
  return createTheme({
    palette,
    ...commonThemeOptions,
  });
};

export const lightTheme = createAppTheme('light');
export const darkTheme = createAppTheme('dark');

const themeExports = { lightTheme, darkTheme, createAppTheme };
export default themeExports;