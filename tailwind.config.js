/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.html',
    './src/**/*.ts',
  ],
  theme: {
    extend: {
      colors: {
        primary: '#e1fff1',
        'mint-accent': '#81F0CA',
        background: '#0f1512',
        'surface-elevated': '#1A1F21',
        'surface-cream': '#d3d3d3',
        'deep-carbon': '#0B0F10',
        'text-secondary': '#0f1512',
        'on-surface-variant': '#bccac2',
      },
      spacing: {
        gutter: '32px',
        'margin-mobile': '24px',
        'container-max': '1440px',
        'section-gap': '160px',
        'margin-desktop': '80px',
      },
      fontFamily: {
        'headline-lg': ['Bricolage Grotesque', 'sans-serif'],
        'headline-md': ['Bricolage Grotesque', 'sans-serif'],
        'display-xl': ['Bricolage Grotesque', 'sans-serif'],
        'body-lg': ['Hanken Grotesque', 'sans-serif'],
        'body-md': ['Hanken Grotesque', 'sans-serif'],
        'label-sm': ['JetBrains Mono', 'monospace'],
      },
      fontSize: {
        'display-xl': ['96px', { lineHeight: '100px', letterSpacing: '-0.04em', fontWeight: '800' }],
        'headline-lg': ['64px', { lineHeight: '72px', letterSpacing: '-0.02em', fontWeight: '800' }],
        'headline-md': ['32px', { lineHeight: '40px', fontWeight: '700' }],
        'body-lg': ['20px', { lineHeight: '32px', fontWeight: '400' }],
        'body-md': ['16px', { lineHeight: '24px', fontWeight: '400' }],
        'label-sm': ['12px', { lineHeight: '16px', letterSpacing: '0.1em', fontWeight: '500' }],
      },
      borderRadius: {
        DEFAULT: '0.25rem',
        xl: '0.75rem',
        full: '9999px',
      },
    },
  },
  plugins: [],
};
