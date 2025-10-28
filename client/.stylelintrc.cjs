module.exports = {
  // Prevent Stylelint from flagging Tailwind's at-rules like @tailwind and @apply
  // This keeps your editor/linter quiet while PostCSS + Tailwind still process them at build time.
  rules: {
    "at-rule-no-unknown": [true, {
      ignoreAtRules: [
        "tailwind",
        "apply",
        "variants",
        "responsive",
        "screen",
        "layer"
      ]
    }]
  },
  ignoreFiles: ["**/node_modules/**"]
};
