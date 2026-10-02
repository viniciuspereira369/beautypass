---
name: Serene Mint & Teal Beauty Marketplace
colors:
  surface: '#e4fffb'
  surface-dim: '#ade5df'
  surface-bright: '#e4fffb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#c6fef8'
  surface-container: '#c1f9f3'
  surface-container-high: '#bbf3ed'
  surface-container-highest: '#b5ede7'
  on-surface: '#00201e'
  on-surface-variant: '#3d4947'
  inverse-surface: '#003734'
  inverse-on-surface: '#c3fcf5'
  outline: '#6d7a77'
  outline-variant: '#bcc9c6'
  surface-tint: '#006a61'
  primary: '#00685f'
  on-primary: '#ffffff'
  primary-container: '#008378'
  on-primary-container: '#f4fffc'
  inverse-primary: '#6bd8cb'
  secondary: '#006b5e'
  on-secondary: '#ffffff'
  secondary-container: '#6ef9e2'
  on-secondary-container: '#007164'
  tertiary: '#525e5c'
  on-tertiary: '#ffffff'
  tertiary-container: '#6b7775'
  on-tertiary-container: '#f3fffc'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#89f5e7'
  primary-fixed-dim: '#6bd8cb'
  on-primary-fixed: '#00201d'
  on-primary-fixed-variant: '#005049'
  secondary-fixed: '#6ef9e2'
  secondary-fixed-dim: '#4ddcc6'
  on-secondary-fixed: '#00201b'
  on-secondary-fixed-variant: '#005047'
  tertiary-fixed: '#d8e5e2'
  tertiary-fixed-dim: '#bcc9c6'
  on-tertiary-fixed: '#121e1c'
  on-tertiary-fixed-variant: '#3d4947'
  background: '#e4fffb'
  on-background: '#00201e'
  surface-variant: '#b5ede7'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-sm: 0.75rem
  gutter-lg: 1.5rem
  margin: 1rem
  margin-sm: 0.75rem
  margin-lg: 1.5rem
  space-2xs: 0.125rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1rem
  space-xl: 1.5rem
  space-2xl: 2rem
  space-3xl: 2.5rem
---

## Brand & Style
This design system defines an ultra-clean, clinical-yet-inviting aesthetic tailored for an upscale beauty and wellness services marketplace. The experience centers on hygiene, precision, and tranquility, projecting uncompromising professional trust while remaining warm and accessible.

The visual style blends refined modern minimalism with tactile depth:
- Pure, airy negative space mimicking luxury spa sanctuaries.
- Soft, pill-curved surfaces that feel approachable, ergonomic, and organic to the touch.
- Subtle glassmorphic layers on floating navigation elements and sticky filter bars to maintain spatial context without visual clutter.
- Elevated editorial hierarchy that treats stylists, treatments, and salons with premium respect.

## Colors
The palette balances antiseptic precision with soothing spa warmth.

- **Primary (`#0D9488`):** Deep, vibrant teal representing clinical competence, pristine hygiene, and elevated care. Used for primary CTAs, active states, key verification badges, and primary rating accents.
- **Secondary (`#5EEAD4`):** Soft, luminous mint accent. Applied to promotional highlight tags, glow backdrops, step-completion rings, and dynamic micro-interactions.
- **Tertiary (`#F0FDFA`):** Sheer aqua-tinted white base surface. Used for card surfaces, pill backgrounds, and subtle tint layers to replace stark, harsh neutral grays.
- **Neutral (`#134E4A`):** Deep oceanic charcoal-teal. Replaces pure black (`#000000`) across all body copy, headers, and icon strokes to keep contrast high while eliminating clinical harshness.

### Functional Surfaces
- **Canvas Base:** `#FAFCFC` (Fresh milk porcelain).
- **Surface Elevation 1:** `#FFFFFF` (Crisp salon white).
- **Surface Elevated Tint:** `#F0FDFA` with 60% opacity.
- **Muted Text / Metadata:** `#5A7A78`.
- **Subtle Dividing Lines:** `#E6F4F1`.
- **Success / Verified:** `#0D9488`.
- **Promotion / Alert:** `#F43F5E` (Warm coral for sale prices and limited slots).

## Typography
Plus Jakarta Sans is utilized across all typographic roles. Its geometric underpinnings paired with modern, friendly terminals create clean legibility on high-density mobile screens while infusing warmth into beauty service menus and stylist profiles.

- **Numerics & Pricing:** Use `fontWeight: 700` with tabular figures where possible to keep pricing grids and duration tags aligned.
- **Editorial Headings:** Use tight negative letter spacing on `headline-lg` and `display-lg` to create a deliberate, magazine-like look.
- **Badges and Status Tags:** Use `label-sm` in all-caps or title case with expanded tracking (`0.04em`) to ensure legibility when overlaid on photography.

## Layout & Spacing
The layout relies on a mobile-first, 4-column fluid grid system expanding to 8 columns on tablet viewports and 12 columns on desktop panels:

- **Mobile Canvas (up to 480px):** 4 columns, 16px (`1rem`) outer margin, 12px (`0.75rem`) gutter. Edge-to-edge scrolling carousel cards bleed slightly over the right margin with an offset padding of 16px.
- **Tablet (481px - 840px):** 8 columns, 24px (`1.5rem`) outer margin, 16px (`1rem`) gutter. Service menus and booking flows transition into dual-column cards.
- **Desktop / Admin Hub (841px+):** Max-width container clamped at 1200px with a 12-column grid, centered with variable auto margins.

Vertical rhythm adheres to an 8px baseline grid (with 4px half-steps reserved for tight badge metadata). Component padding scales from internal chips (`space-xs` and `space-sm`) to card containers (`space-lg` and `space-xl`).

## Elevation & Depth
Depth is rendered through diffused, light-refracting ambient teal shadows rather than heavy neutral drop shadows. Surfaces feel weightless, sterile, and float softly above the canvas.

- **Level 0 (Flat):** Used for input backgrounds and passive tags. `#F4FAF9` base with zero shadow.
- **Level 1 (Cards & Service Tiles):** Background `#FFFFFF` with `box-shadow: 0 4px 16px -2px rgba(13, 148, 136, 0.06), 0 2px 6px -1px rgba(19, 78, 74, 0.03)`. Outlined with a microscopic `1px solid rgba(13, 148, 136, 0.08)` border.
- **Level 2 (Popovers, Active Bottom Sheets, Dropdowns):** Background `#FFFFFF` with `box-shadow: 0 12px 32px -4px rgba(13, 148, 136, 0.12), 0 4px 12px -2px rgba(19, 78, 74, 0.05)`.
- **Level 3 (Floating Action Buttons & Bottom Navigation Bar):** Background `rgba(255, 255, 255, 0.88)` with `backdrop-filter: blur(16px)` and `box-shadow: 0 16px 40px -6px rgba(13, 148, 136, 0.16)`.

## Shapes
The shape hierarchy emphasizes organic, approachable ergonomics:

- **Core Elements (Level 2 Roundedness):** Standard buttons, treatment cards, text inputs, and salon detail surfaces feature an 8px (`0.5rem`) radius.
- **Hero & Card Groups (`rounded-lg` / `rounded-xl`):** Primary salon booking preview cards and bottom sheet modal corners employ 16px (`1rem`) to 24px (`1.5rem`) radii.
- **Interactive Badges & Pills:** Filter pills, promotional badges, and floating booking toggles use full capsules (`9999px` / pill-shaped) to communicate tap readiness and fluidity.

## Components

### Buttons
- **Primary:** Solid `#0D9488` with `#FFFFFF` text. Height: 48px on mobile, full width for booking triggers. Subtle inner highlight line (`inset 0 1px 0 rgba(255, 255, 255, 0.2)`).
- **Secondary / Soft:** Tinted `#F0FDFA` background with `#0D9488` label and active states scaling slightly on tap (`transform: scale(0.98)`).
- **Ghost:** Transparent background with `#0D9488` text and icon for secondary actions like "View All Reviews".

### Badges & Status Indicators
- **Verified Salon Badge:** Capsule shape with `#0D9488` background, `#FFFFFF` text, paired with a white checkmark icon. Tiny, crisp footprint (height: 20px).
- **Promotional / Deal Badge:** `#FFF1F2` soft coral background with `#E11D48` bold text denoting discounts (e.g., "-20% OFF") or "Popular".
- **Rating Chip:** Pill container with `#F0FDFA` background, featuring an amber star icon (`#F59E0B`) next to the rating score in `label-sm` bold.

### Cards
- **Salon & Service Card:** Crisp `#FFFFFF` surface, `rounded-xl` (24px) border radius. Edge-to-edge 16:9 imagery with an overlaid gradient scrim at the top for distance/price badges. Padding inside the content area is 16px. Service tags rest along the footer as horizontal mini-chips.
- **Time Slot Selector Card:** Compact rounded blocks (64px wide × 56px high) with neutral border; selected state fills with `#0D9488` and inverts text to white.

### Inputs & Search Bars
- **Marketplace Search Bar:** Pill-shaped (`rounded-full`), height 48px, background `#FFFFFF` with Level 1 teal shadow. Prefixed by a teal search glyph and suffixed by a vertical divider leading to an icon-only filter button.
- **Text Inputs:** Floating label style with `#FAFCFC` background and a gentle `#E6F4F1` border that transitions to a 2px `#0D9488` ring on focus.

### Elevated Mobile Navigation Bar
- Docked to bottom viewport, floating with an 8px margin from the screen bottom and 16px side margins.
- Translucent frosted white background (`rgba(255, 255, 255, 0.92)`) with `backdrop-filter: blur(18px)`.
- Active tabs generate a subtle glowing mint dot (`#5EEAD4`) beneath the active teal icon.