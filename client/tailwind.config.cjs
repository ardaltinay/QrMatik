module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#f5f6ff",
          100: "#eceffe",
          300: "#c9ccfb",
          500: "#6366F1",
          700: "#4f46e5",
        },
      },
      fontFamily: {
        sans: ["Inter", "ui-sans-serif", "system-ui"],
      },
    },
    keyframes: {
      fadeIn: {
        "0%": { opacity: "0", transform: "translateY(-6px)" },
        "100%": { opacity: "1", transform: "translateY(0)" },
      },
      slideDown: {
        "0%": { opacity: "0", transform: "translateY(-10px)" },
        "100%": { opacity: "1", transform: "translateY(0)" },
      },
      slideUp: {
        "0%": { opacity: "0", transform: "translateY(10px)" },
        "100%": { opacity: "1", transform: "translateY(0)" },
      },
    },
    animation: {
      fadeIn: "fadeIn 220ms ease-out",
      slideDown: "slideDown 220ms ease-out",
      slideUp: "slideUp 260ms cubic-bezier(.2,.8,.2,1)",
    },
  },
  plugins: [],
};
