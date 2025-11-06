// Reusable formatting utilities

// Internal helper: parse various input shapes to a Date reliably, or return null
function parseBestDate(input) {
  try {
    if (!input) return null;
    if (input instanceof Date) return isNaN(input) ? null : input;
    if (typeof input === "number") {
      const d = new Date(input);
      return isNaN(d) ? null : d;
    }
    if (typeof input === "object") {
      const y = input.year || input.y;
      const mo = (input.monthValue || input.month || 1) - 1;
      const d = input.dayOfMonth || input.day || 1;
      const h = input.hour || 0;
      const mi = input.minute || 0;
      const se = input.second || 0;
      if (y) {
        const dt = new Date(y, mo, d, h, mi, se);
        return isNaN(dt) ? null : dt;
      }
    }
    if (typeof input === "string") {
      let s = input;
      let d = new Date(s);
      if (!isNaN(d)) return d;
      if (s.includes(" ")) {
        d = new Date(s.replace(" ", "T"));
        if (!isNaN(d)) return d;
      }
      if (!/[zZ]|[+-]\d{2}:?\d{2}$/.test(s)) {
        d = new Date(s + "Z");
        if (!isNaN(d)) return d;
      }
      const msIdx = s.indexOf(".");
      if (msIdx > 0) {
        const base = s.substring(0, msIdx);
        const tzIndex = Math.max(s.indexOf("Z"), s.search(/[+-]\d{2}:?\d{2}$/));
        const tz = tzIndex >= 0 ? s.substring(tzIndex) : "";
        d = new Date(base + tz || base);
        if (!isNaN(d)) return d;
      }
    }
  } catch {
    // ignore
  }
  return null;
}

// Deprecated: formatDate removed in favor of formatDateTz to ensure consistent tenant timezone display

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

// Prefer this helper when presenting absolute instants as local Turkish time
export function formatDateTz(input, locale = undefined, timeZone = undefined) {
  try {
    // Read defaults from tenant config if available
    if (!locale) {
      try {
        locale = localStorage.getItem("qm_locale") || "tr-TR";
      } catch {
        locale = "tr-TR";
      }
    }
    if (!timeZone) {
      try {
        timeZone = localStorage.getItem("qm_tz") || "Europe/Istanbul";
      } catch {
        timeZone = "Europe/Istanbul";
      }
    }
    const d = parseBestDate(input);
    if (!d) return String(input || "-");
    return new Intl.DateTimeFormat(locale, {
      timeZone,
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    }).format(d);
  } catch {
    return String(input || "-");
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
      bill_requested: "Hesap İstendi",
      payment_completed: "Ödeme Tamamlandı",
      canceled: "İptal Edildi",
      expired: "Süresi Doldu",
    };
    return map[key] || s || "-";
  } catch {
    return String(s || "-");
  }
}
