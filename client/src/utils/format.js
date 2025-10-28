// Reusable formatting utilities

export function formatDate(s) {
  try {
    if (!s) return "-";
    if (typeof s === "number") return new Date(s).toLocaleString();
    if (typeof s === "object") {
      const y = s.year || s.y;
      const mo = (s.monthValue || s.month || 1) - 1;
      const d = s.dayOfMonth || s.day || 1;
      const h = s.hour || 0;
      const mi = s.minute || 0;
      const se = s.second || 0;
      if (y) return new Date(y, mo, d, h, mi, se).toLocaleString();
    }
    if (typeof s === "string") {
      let d = new Date(s);
      if (!isNaN(d)) return d.toLocaleString();
      if (s.includes(" ")) {
        d = new Date(s.replace(" ", "T"));
        if (!isNaN(d)) return d.toLocaleString();
      }
      if (!/[zZ]|[+-]\d{2}:?\d{2}$/.test(s)) {
        d = new Date(s + "Z");
        if (!isNaN(d)) return d.toLocaleString();
      }
      const msIdx = s.indexOf(".");
      if (msIdx > 0) {
        const base = s.substring(0, msIdx);
        const tzIndex = Math.max(s.indexOf("Z"), s.search(/[+-]\d{2}:?\d{2}$/));
        const tz = tzIndex >= 0 ? s.substring(tzIndex) : "";
        d = new Date(base + tz || base);
        if (!isNaN(d)) return d.toLocaleString();
      }
    }
    return String(s);
  } catch {
    return String(s || "-");
  }
}

export function orderCodeFromId(id) {
  const s = id != null ? String(id) : "";
  if (!s) return "-";
  if (/^\d+$/.test(s)) return s;
  return s.replace(/-/g, "").slice(-6).toUpperCase();
}

export function primaryLabel(p) {
  const map = { food: "Yiyecekler", drink: "İçecekler", drinks: "İçecekler" };
  return map[p] || p;
}

export function categoryLabel(cat) {
  return primaryLabel(cat);
}

export function subLabel(s) {
  const map = {
    starter: "Başlangıç",
    main: "Ana Yemek",
    dessert: "Tatlı",
    "non-alcoholic": "Alkolsüz",
    alcoholic: "Alkollü",
    pizza: "Pizza",
    salad: "Salata",
    soda: "Gazlı İçecek",
    wine: "Şarap",
  };
  return map[s] || s;
}

export function formatMoney(value, currency = "TRY") {
  try {
    const num = typeof value === "number" ? value : Number(value || 0);
    return new Intl.NumberFormat("tr-TR", { style: "currency", currency }).format(num);
  } catch {
    const num = typeof value === "number" ? value : Number(value || 0);
    return num.toFixed(2) + " ₺";
  }
}

export function statusLabel(s) {
  try {
    const key = String(s || "").toLowerCase();
    const map = {
      new: "Yeni",
      preparing: "Hazırlanıyor",
      ready: "Hazır",
      served: "Servis Edildi",
      payment_completed: "Ödeme Tamamlandı",
    };
    return map[key] || s || "-";
  } catch {
    return String(s || "-");
  }
}
