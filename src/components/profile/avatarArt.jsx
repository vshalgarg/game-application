const Portrait = ({ bg, children }) => (
  <svg viewBox="0 0 96 96" xmlns="http://www.w3.org/2000/svg" className="h-full w-full" aria-hidden="true">
    <circle cx="48" cy="48" r="48" fill={bg} />
    {children}
  </svg>
);

export const NovaArt = () => (
  <Portrait bg="#12324f">
    <path d="M22 78c4-18 14-28 26-28s22 10 26 28" fill="#0e243b" />
    <ellipse cx="48" cy="44" rx="20" ry="22" fill="#1b4d6e" />
    <ellipse cx="48" cy="46" rx="16" ry="18" fill="#e8b894" />
    <circle cx="42" cy="45" r="2.1" fill="#172a46" />
    <circle cx="54" cy="45" r="2.1" fill="#172a46" />
    <path d="M42 54c2.2 2.4 9.8 2.4 12 0" stroke="#c4785a" strokeWidth="1.6" fill="none" strokeLinecap="round" />
    <path d="M28 44c0-14 9-24 20-26 11 2 20 12 20 26" stroke="#00d9e8" strokeWidth="3.2" fill="none" />
    <rect x="24" y="40" width="8" height="14" rx="4" fill="#00d9e8" />
    <rect x="64" y="40" width="8" height="14" rx="4" fill="#00d9e8" />
    <path d="M32 42c4-10 12-14 16-15 4 1 12 5 16 15" fill="#1a3f5c" />
  </Portrait>
);

export const BlazeArt = () => (
  <Portrait bg="#3a1d3a">
    <path d="M20 80c6-20 16-30 28-30s22 10 28 30" fill="#2a142c" />
    <path d="M26 46c2-22 10-32 22-34 12 2 20 12 22 34-6-8-14-10-22-10s-16 2-22 10z" fill="#e85d4c" />
    <ellipse cx="48" cy="48" rx="16" ry="18" fill="#f0c3a0" />
    <circle cx="42" cy="47" r="2.1" fill="#172a46" />
    <circle cx="54" cy="47" r="2.1" fill="#172a46" />
    <path d="M41 55c3 3.2 11 3.2 14 0" stroke="#c4785a" strokeWidth="1.6" fill="none" strokeLinecap="round" />
    <path d="M30 38c6-8 12-12 18-13 6 1 12 5 18 13" fill="#ff7a59" />
  </Portrait>
);

export const LunaArt = () => (
  <Portrait bg="#24164a">
    <path d="M18 82c8-26 16-38 30-38s22 12 30 38" fill="#1a0f38" />
    <path d="M22 70c2-32 8-48 26-52 18 4 24 20 26 52-10-16-18-18-26-18s-16 2-26 18z" fill="#8b5cf6" />
    <ellipse cx="48" cy="48" rx="15.5" ry="17.5" fill="#f3c7b0" />
    <circle cx="42" cy="47" r="2" fill="#172a46" />
    <circle cx="54" cy="47" r="2" fill="#172a46" />
    <path d="M42 54.5c2 2.2 10 2.2 12 0" stroke="#c4785a" strokeWidth="1.5" fill="none" strokeLinecap="round" />
    <path d="M33 44c4-12 9-16 15-17 6 1 11 5 15 17" fill="#6d3fd0" />
  </Portrait>
);

export const PixelArt = () => (
  <Portrait bg="#123c38">
    <path d="M22 80c5-20 14-29 26-29s21 9 26 29" fill="#0d2c2a" />
    <path d="M28 42c3-16 9-24 20-25 11 1 17 9 20 25-5-6-12-8-20-8s-15 2-20 8z" fill="#34d399" />
    <ellipse cx="48" cy="48" rx="16" ry="17.5" fill="#e6b089" />
    <rect x="34" y="42" width="12" height="8" rx="2" fill="none" stroke="#00d9e8" strokeWidth="1.8" />
    <rect x="50" y="42" width="12" height="8" rx="2" fill="none" stroke="#00d9e8" strokeWidth="1.8" />
    <path d="M46 46h4" stroke="#00d9e8" strokeWidth="1.8" />
    <circle cx="40" cy="46" r="1.6" fill="#172a46" />
    <circle cx="56" cy="46" r="1.6" fill="#172a46" />
    <path d="M42 55c2.4 2 9.6 2 12 0" stroke="#c4785a" strokeWidth="1.5" fill="none" strokeLinecap="round" />
  </Portrait>
);

export const AceArt = () => (
  <Portrait bg="#1b2748">
    <path d="M21 80c5-19 14-28 27-28s22 9 27 28" fill="#121a33" />
    <ellipse cx="48" cy="42" rx="20" ry="20" fill="#111827" />
    <ellipse cx="48" cy="47" rx="16" ry="17.5" fill="#c99570" />
    <circle cx="42" cy="46" r="2.1" fill="#172a46" />
    <circle cx="54" cy="46" r="2.1" fill="#172a46" />
    <path d="M41 54c3 2.8 11 2.8 14 0" stroke="#a56b4a" strokeWidth="1.6" fill="none" strokeLinecap="round" />
    <path d="M28 44c1-13 9-23 20-25 11 2 19 12 20 25" stroke="#f5c542" strokeWidth="2.6" fill="none" />
    <rect x="24.5" y="40" width="7.5" height="13" rx="3.5" fill="#f5c542" />
    <rect x="64" y="40" width="7.5" height="13" rx="3.5" fill="#f5c542" />
    <circle cx="48" cy="70" r="3.2" fill="#f5c542" />
  </Portrait>
);

export const SageArt = () => (
  <Portrait bg="#163326">
    <path d="M20 82c7-22 16-32 28-32s21 10 28 32" fill="#10241c" />
    <path d="M24 58c2-24 10-38 24-40 14 2 22 16 24 40-8-10-16-12-24-12s-16 2-24 12z" fill="#2f6f4e" />
    <ellipse cx="48" cy="50" rx="15" ry="16" fill="#d9a074" />
    <circle cx="42" cy="49" r="2" fill="#172a46" />
    <circle cx="54" cy="49" r="2" fill="#172a46" />
    <path d="M42 56.5c2 1.8 10 1.8 12 0" stroke="#b07050" strokeWidth="1.5" fill="none" strokeLinecap="round" />
    <path d="M30 44c6-8 11-10 18-10s12 2 18 10" fill="#245c40" />
  </Portrait>
);

export const RexArt = () => (
  <Portrait bg="#0f2744">
    <path d="M22 80c5-18 14-27 26-27s21 9 26 27" fill="#0b1c31" />
    <path d="M44 14l4 22 4-22c8 8 14 20 16 32-6-4-12-6-20-6s-14 2-20 6c2-12 8-24 16-32z" fill="#00d9e8" />
    <ellipse cx="48" cy="48" rx="16" ry="18" fill="#e0aa86" />
    <circle cx="42" cy="47" r="2.1" fill="#172a46" />
    <circle cx="54" cy="47" r="2.1" fill="#172a46" />
    <path d="M40 55c3.4 3.4 12.6 3.4 16 0" stroke="#c4785a" strokeWidth="1.6" fill="none" strokeLinecap="round" />
    <path d="M34 38c4-6 9-8 14-8s10 2 14 8" fill="#0891b2" />
  </Portrait>
);

export const MiraArt = () => (
  <Portrait bg="#3b1844">
    <path d="M20 82c7-24 16-36 28-36s21 12 28 36" fill="#2a1032" />
    <circle cx="62" cy="30" r="8" fill="#f472b6" />
    <circle cx="34" cy="32" r="7" fill="#f472b6" />
    <ellipse cx="48" cy="36" rx="16" ry="12" fill="#f9a8d4" />
    <ellipse cx="48" cy="48" rx="15.5" ry="17" fill="#f0c3a8" />
    <circle cx="42" cy="47" r="2" fill="#172a46" />
    <circle cx="54" cy="47" r="2" fill="#172a46" />
    <path d="M42 54c2.2 2.4 9.8 2.4 12 0" stroke="#c4785a" strokeWidth="1.5" fill="none" strokeLinecap="round" />
    <path d="M34 40c4-10 9-14 14-14s10 4 14 14" fill="#ec4899" />
  </Portrait>
);
