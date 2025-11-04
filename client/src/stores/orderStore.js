import { defineStore } from "pinia";
import { ref } from "vue";
import { useUiStore } from "@/stores/uiStore";
import { apiFetch, fetchJson } from "@/utils/api";

// Simple demo store: in-memory; in real app replace with API calls/websockets
export const useOrderStore = defineStore("order", () => {
  const menu = ref([]);
  const menuLoaded = ref(false);
  let menuLoading = null;

  // Load menu from persistent cache (per-tenant) to avoid redundant fetches across reloads
  try {
    const tenant = typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
    if (tenant) {
      const cached =
        typeof localStorage !== "undefined"
          ? localStorage.getItem("qm_menu_cache_" + tenant)
          : null;
      if (cached) {
        try {
          const obj = JSON.parse(cached);
          if (obj && Array.isArray(obj.menu)) {
            menu.value = obj.menu;
            menuLoaded.value = true;
          }
        } catch {
          /* ignore parse issues */
        }
      }
    }
  } catch {
    /* ignore storage issues */
  }

  const cart = ref([]); // {itemId, qty, note?}
  // Simple cookie helpers to share order session across subdomains
  function getBaseDomain() {
    try {
      const h = window.location.hostname || "";
      if (!h) return null;
      // Share across *.localhost by setting Domain=.localhost
      if (h === "localhost" || h.endsWith(".localhost")) return "localhost";
      const parts = h.split(".");
      if (parts.length >= 2) {
        // naive: take last 2 parts (example.com). For multi-level TLDs (co.uk), consider customizing if needed.
        return parts.slice(-2).join(".");
      }
      return h;
    } catch {
      return null;
    }
  }
  function setCookie(name, value, maxAgeSeconds) {
    try {
      let cookie = encodeURIComponent(name) + "=" + encodeURIComponent(value) + "; Path=/";
      // Enforce minimum 3 hours for order session persistence across subdomains
      const MIN_SECONDS = 3 * 60 * 60; // 3 hours
      const eff =
        typeof maxAgeSeconds === "number" ? Math.max(maxAgeSeconds, MIN_SECONDS) : MIN_SECONDS;
      cookie += "; Max-Age=" + String(eff);
      const base = getBaseDomain();
      if (base && base.includes(".")) cookie += "; Domain=." + base;
      document.cookie = cookie;
    } catch {
      /* ignore */
    }
  }
  function deleteCookie(name) {
    try {
      const base = getBaseDomain();
      let cookie = encodeURIComponent(name) + "=; Max-Age=0; Path=/";
      if (base && base.includes(".")) cookie += "; Domain=." + base;
      document.cookie = cookie;
    } catch {
      /* ignore */
    }
  }
  function getCookie(name) {
    try {
      const n = encodeURIComponent(name) + "=";
      const parts = (document.cookie || "").split("; ");
      for (const p of parts) {
        if (p.startsWith(n)) return decodeURIComponent(p.substring(n.length));
      }
      return null;
    } catch {
      return null;
    }
  }

  const orders = ref([]);
  const ordersById = ref({}); // cache by id -> normalized order
  let nextOrderId = 1000;
  // BroadcastChannel for simple cross-tab realtime demo
  let bc = null;
  if (typeof window !== "undefined" && "BroadcastChannel" in window) {
    bc = new BroadcastChannel("orders");
    bc.onmessage = (ev) => {
      const { type, payload } = ev.data || {};
      if (type === "create") {
        // avoid duplicating if already have
        if (!orders.value.find((o) => o.id === payload.id)) orders.value.push(payload);
      }
      if (type === "update") {
        const o = orders.value.find((x) => x.id === payload.id);
        if (o) o.status = payload.status;
      }
    };
  }

  // load menu from server (with fallback)
  async function loadMenu(options = {}) {
    const force = !!options.force;
    // already loaded: return fast but do a background refresh to avoid stale cache
    if (!force && (menuLoaded.value || (Array.isArray(menu.value) && menu.value.length > 0))) {
      if (!menuLoading) {
        menuLoading = (async () => {
          try {
            const data = await fetchJson("/api/menu");
            const fresh = (Array.isArray(data) ? data : []).map((it) => ({
              id: it.id,
              name: it.name,
              price: typeof it.price === "number" ? it.price : Number(it.price || 0),
              category: it.category || "Genel",
              primary:
                it.primary ??
                it.primaryType ??
                (it.category && it.category.toLowerCase().includes("drink") ? "drink" : "food"),
              sub: it.sub || it.subcategory || "main",
              image:
                it.image ||
                "https://picsum.photos/seed/menu" + (it.id || Math.random()) + "/400/240",
            }));
            menu.value = fresh;
            // update cache
            try {
              const tenant =
                typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
              if (tenant) {
                const payload = { ts: Date.now(), menu: menu.value };
                localStorage.setItem("qm_menu_cache_" + tenant, JSON.stringify(payload));
              }
            } catch {
              /* ignore */
            }
          } catch {
            /* ignore */
          } finally {
            menuLoading = null;
          }
        })();
      }
      return;
    }
    // if a request is in-flight, await it
    if (!force && menuLoading) {
      try {
        await menuLoading;
      } catch {
        /* ignore */
      }
      return;
    }
    // start single in-flight request
    menuLoading = (async () => {
      try {
        const data = await fetchJson("/api/menu");
        menu.value = (Array.isArray(data) ? data : []).map((it) => ({
          id: it.id,
          name: it.name,
          price: typeof it.price === "number" ? it.price : Number(it.price || 0),
          category: it.category || "Genel",
          primary:
            it.primary ??
            it.primaryType ??
            (it.category && it.category.toLowerCase().includes("drink") ? "drink" : "food"),
          sub: it.sub || it.subcategory || "main",
          image:
            it.image || "https://picsum.photos/seed/menu" + (it.id || Math.random()) + "/400/240",
        }));
        menuLoaded.value = true;
        // persist normalized menu per tenant (only if tenant resolved)
        try {
          const tenant =
            typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
          if (tenant) {
            const payload = { ts: Date.now(), menu: menu.value };
            localStorage.setItem("qm_menu_cache_" + tenant, JSON.stringify(payload));
          }
        } catch {
          /* ignore storage issues */
        }
      } catch (err) {
        console.debug("Failed to fetch menu", err);
        // Do not inject demo fallback to prevent cross-tenant confusion; leave menu empty
        menu.value = [];
        menuLoaded.value = true;
      } finally {
        menuLoading = null;
      }
    })();
    try {
      await menuLoading;
    } catch {
      /* ignore */
    }
  }

  function clearMenuCache() {
    try {
      const tenant = typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
      if (tenant) localStorage.removeItem("qm_menu_cache_" + tenant);
    } catch {
      /* ignore */
    }
  }

  async function refreshMenu() {
    clearMenuCache();
    menu.value = [];
    menuLoaded.value = false;
    await loadMenu({ force: true });
  }

  // load recent orders from server (admin/dev)
  async function loadOrders() {
    try {
      const data = await fetchJson("/api/orders");
      const mapped = data.map((o) => {
        const items = (Array.isArray(o.lines) ? o.lines : []).map((l) => ({
          itemId: l.itemId,
          qty: l.quantity,
          name: l.name,
          price: l.price,
          note: l.note,
          category: l.category,
          subcategory: l.subcategory,
        }));
        // Heuristics: kategori ya da alt kategori içecek/bar'a işaret ediyorsa 'bar' sayalım
        const hasDrink = items.some((it) => {
          const c = String(it.category || "").toLowerCase();
          const s = String(it.subcategory || "").toLowerCase();
          return (
            c.includes("drink") ||
            c.includes("içecek") ||
            c.includes("icecek") ||
            c.includes("bar") ||
            s.includes("drink") ||
            s.includes("içecek") ||
            s.includes("icecek") ||
            s.includes("bar")
          );
        });
        const type = hasDrink ? "bar" : "kitchen";
        return {
          id: o.id,
          table: o.tableCode || o.table || "guest",
          items,
          type,
          status: (o.status || "NEW").toLowerCase(),
          createdAt: o.createdAt || o.createdTime || new Date().toISOString(),
          total: typeof o.total === "number" ? o.total : Number(o.total || 0),
        };
      });
      // En yeni siparişler üstte
      mapped.sort((a, b) => {
        const ta = new Date(a.createdAt || 0).getTime() || 0;
        const tb = new Date(b.createdAt || 0).getTime() || 0;
        return tb - ta;
      });
      orders.value = mapped;
      for (const o of mapped) {
        ordersById.value[String(o.id)] = o;
      }
      // set nextOrderId higher than max
      const maxId = orders.value.reduce((m, x) => Math.max(m, x.id || 0), 0);
      nextOrderId = maxId + 1;
    } catch (err) {
      console.debug("Failed to fetch orders from server", err);
      // leave orders empty as fallback
    }
  }

  // not auto-loading to avoid redundant requests; views call loadMenu lazily when needed

  function addToCart(itemId, note = "") {
    const keyNote = String(note || "").trim();
    const existing = cart.value.find(
      (i) => i.itemId === itemId && String(i.note || "").trim() === keyNote,
    );
    if (existing) existing.qty++;
    else cart.value.push({ itemId, qty: 1, note: keyNote || undefined });
  }

  function removeFromCart(itemId, note = "") {
    const keyNote = String(note || "").trim();
    cart.value = cart.value.filter(
      (i) => !(i.itemId === itemId && String(i.note || "").trim() === keyNote),
    );
  }

  function clearCart() {
    cart.value = [];
  }

  function createOrder(table = null) {
    if (cart.value.length === 0) return null;
    // Cross-tenant session guard: if there's an existing customer session bound to another tenant, block and show a helpful message
    try {
      const ui = useUiStore();
      const currentTenant =
        typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
      const existingSession =
        typeof localStorage !== "undefined" ? localStorage.getItem("qm_order_session") : null;
      const sessionTenant =
        typeof localStorage !== "undefined"
          ? localStorage.getItem("qm_order_session_tenant")
          : null;
      if (existingSession && sessionTenant && currentTenant && sessionTenant !== currentTenant) {
        ui.toastError(
          "Bu cihazda açık bir sipariş oturumu başka bir restoran için. Bu oturum tamamlanmadan farklı restorandan sipariş veremezsiniz.",
          6000,
        );
        return null;
      }
    } catch {
      /* ignore */
    }
    const items = cart.value.map((i) => ({ ...i }));
    // enrich with snapshot of name and price to avoid menu fetches on tracking page
    const enrichedItems = items.map((i) => {
      const mi = menu.value.find((m) => m.id === i.itemId);
      return Object.assign({}, i, { name: mi?.name, price: mi?.price, note: i.note });
    });
    // calculate total locally (snapshot)
    const calcTotal = enrichedItems.reduce(
      (sum, it) => sum + Number(it.price || 0) * (it.qty || 1),
      0,
    );
    const order = {
      id: nextOrderId++,
      // tableCode bulunamazsa varsayılan 'guest' (sunucuya gönderilmez)
      table: table || "guest",
      items: enrichedItems,
      status: "new", // new -> preparing -> ready -> served
      createdAt: new Date().toISOString(),
      total: calcTotal,
    };
    orders.value.push(order);
    // try to POST to server; include existing sessionId if present
    try {
      const payload = {
        status: order.status,
        createdAt: order.createdAt,
        lines: enrichedItems.map((i) => ({
          itemId: i.itemId,
          quantity: i.qty,
          price: i.price,
          name: i.name,
          note: i.note,
        })),
      };
      // tableCode sadece geçerli ve mevcut tenant'a aitse gönderilsin
      try {
        const currentTenant =
          typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
        const tableTenant =
          typeof localStorage !== "undefined" ? localStorage.getItem("qm_table_tenant") : null;
        const canSendTable =
          order.table &&
          order.table !== "guest" &&
          (!currentTenant || !tableTenant || tableTenant === currentTenant);
        if (canSendTable) payload.tableCode = order.table;
      } catch {
        /* ignore */
      }
      const existingSession = (() => {
        try {
          let sid = localStorage.getItem("qm_order_session");
          if (!sid && typeof document !== "undefined") sid = getCookie("qm_order_session");
          return sid;
        } catch {
          return null;
        }
      })();
      if (existingSession) payload.sessionId = existingSession;

      apiFetch("/api/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })
        .then(async (r) => {
          if (!r.ok) {
            // Show a clear message for cross-tenant session attempts and generic for others
            try {
              const ui = useUiStore();
              const currentTenant =
                typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
              const sessionTenant =
                typeof localStorage !== "undefined"
                  ? localStorage.getItem("qm_order_session_tenant")
                  : null;
              const hasSession =
                typeof localStorage !== "undefined"
                  ? !!localStorage.getItem("qm_order_session")
                  : false;
              const isCrossTenantLikely =
                hasSession && currentTenant && (!sessionTenant || sessionTenant !== currentTenant);
              if (r.status === 400 && isCrossTenantLikely) {
                ui.toastError(
                  "Mevcut sipariş oturumunuz farklı bir restorana ait. Bu oturum tamamlanmadan bu restorandan sipariş veremezsiniz.",
                  6000,
                );
              } else {
                ui.toastError("Sipariş gönderilemedi. Lütfen tekrar deneyin.");
              }
            } catch {
              /* ignore */
            }
            return;
          }
          const created = await r.json();
          const idx = orders.value.findIndex((o) => o.id === order.id);
          if (idx >= 0) {
            const serverTotal =
              typeof created.total === "number" ? created.total : Number(created.total || 0);
            orders.value[idx] = {
              ...orders.value[idx],
              id: created.id,
              sessionId: created.sessionId || orders.value[idx].sessionId,
              total: serverTotal || orders.value[idx].total,
            };
          }
          // cache server version from DTO lines
          try {
            const parsedItems = (Array.isArray(created.lines) ? created.lines : []).map((l) => ({
              itemId: l.itemId,
              qty: l.quantity,
              name: l.name,
              price: l.price,
              note: l.note,
            }));
            const normalized = {
              id: created.id,
              table: created.tableCode || created.table || order.table || "guest",
              items: parsedItems,
              status: (created.status || order.status).toLowerCase(),
              createdAt: created.createdAt || created.createdTime || order.createdAt,
              total: typeof created.total === "number" ? created.total : Number(created.total || 0),
            };
            ordersById.value[String(created.id)] = normalized;
          } catch {
            /* ignore */
          }
          // En son sipariş ID'sini gerçek sunucu ID'siyle güncelle
          try {
            localStorage.setItem("qm_last_order", String(created.id));
          } catch {
            /* ignore */
          }
          const sess = r.headers.get("X-Order-Session") || (created && created.sessionId);
          if (sess) {
            try {
              localStorage.setItem("qm_order_session", sess);
            } catch {
              /* ignore */
            }
            // also set cookie to share across subdomains (max-age ~ 24h default)
            try {
              setCookie("qm_order_session", sess, 60 * 60 * 24);
            } catch {
              /* ignore */
            }
            // persist expiry from header or DTO if present
            try {
              const expHeader = r.headers.get("X-Order-Expires");
              const exp =
                expHeader ||
                (created &&
                  (created.sessionExpiresAt || created.sessionExpiry || created.expiresAt));
              if (exp) localStorage.setItem("qm_order_session_expires", String(exp));
            } catch {
              /* ignore */
            }
            // Bind session to current tenant to provide better UX and error messages across subdomains
            try {
              const currentTenant =
                typeof localStorage !== "undefined" ? localStorage.getItem("qm_tenant") : null;
              if (currentTenant) localStorage.setItem("qm_order_session_tenant", currentTenant);
            } catch {
              /* ignore */
            }
            window.dispatchEvent(
              new CustomEvent("qm:orderSession", {
                detail: { sessionId: sess, orderId: created.id },
              }),
            );
          }
        })
        .catch((e) => console.debug("order create network error", e));
    } catch (e) {
      console.debug("order create sync error", e);
    }
    clearCart();
    // persist last order id so customer can return to it
    try {
      localStorage.setItem("qm_last_order", String(order.id));
    } catch {
      console.debug("localStorage not available");
    }
    // also dispatch a storage-like event for same-tab listeners
    try {
      window.dispatchEvent(new CustomEvent("qm:lastOrder", { detail: { id: order.id } }));
    } catch {
      /* ignore */
    }
    if (bc) bc.postMessage({ type: "create", payload: order });
    return order;
  }

  // load all orders for given sessionId
  async function loadSessionOrders(sessionId) {
    if (!sessionId) return [];
    try {
      const res = await apiFetch("/api/orders/session/" + encodeURIComponent(sessionId));
      if (res.status === 410) {
        // session expired on server: clear locally and notify
        try {
          localStorage.removeItem("qm_order_session");
          localStorage.removeItem("qm_order_session_expires");
          localStorage.removeItem("qm_order_session_tenant");
          deleteCookie("qm_order_session");
        } catch {
          /* ignore */
        }
        try {
          window.dispatchEvent(
            new CustomEvent("qm:orderSessionExpired", { detail: { sessionId } }),
          );
        } catch {
          /* ignore */
        }
        return [];
      }
      if (!res.ok) {
        console.debug("Failed to fetch session orders", res.status);
        return [];
      }
      const data = await res.json();
      const mapped = data.map((o) => ({
        id: o.id,
        table: o.tableCode || o.table,
        items: (Array.isArray(o.lines) ? o.lines : []).map((l) => ({
          itemId: l.itemId,
          qty: l.quantity,
          name: l.name,
          price: l.price,
          note: l.note,
        })),
        status: (o.status || "").toLowerCase(),
        createdAt: o.createdAt || o.createdTime,
        total: typeof o.total === "number" ? o.total : Number(o.total || 0),
      }));
      // En güncel siparişler en üstte: createdAt'e göre azalan sırala
      mapped.sort((a, b) => {
        const ta = new Date(a.createdAt || 0).getTime() || 0;
        const tb = new Date(b.createdAt || 0).getTime() || 0;
        return tb - ta;
      });
      // İptal edilenler (canceled) ve süresi dolanlar (expired) listenin en altında kalsın; diğerleri üstte
      const isBottom = (s) => {
        const st = String(s || "").toLowerCase();
        return st === "canceled" || st === "expired";
      };
      const top = mapped.filter((o) => !isBottom(o.status));
      const bottom = mapped.filter((o) => isBottom(o.status));
      const ordered = top.concat(bottom);
      for (const o of ordered) {
        ordersById.value[String(o.id)] = o;
      }
      return ordered;
    } catch (err) {
      console.debug("Failed to fetch session orders", err);
      return [];
    }
  }

  async function updateOrderStatus(orderId, status) {
    // optimistic update for snappy UI
    const normalized = String(status || "").toLowerCase();
    const o = orders.value.find((x) => x.id === orderId);
    if (o) o.status = normalized;
    const key = String(orderId);
    if (ordersById.value[key]) ordersById.value[key].status = normalized;
    if (bc) bc.postMessage({ type: "update", payload: { id: orderId, status: normalized } });

    // persist to server
    try {
      let updated;
      if (normalized === "canceled") {
        // Decide path: if admin context (auth token present or /admin path), use staff endpoint.
        const isAdminPath =
          typeof window !== "undefined" &&
          window.location &&
          String(window.location.pathname || "").startsWith("/admin");
        let hasAuth = false;
        try {
          const raw =
            typeof localStorage !== "undefined" ? localStorage.getItem("qm_session") : null;
          if (raw) {
            const sess = JSON.parse(raw);
            hasAuth = !!(sess && sess.token);
          }
        } catch {
          /* ignore */
        }
        if (isAdminPath || hasAuth) {
          // Staff cancel via status endpoint
          updated = await fetchJson(`/api/orders/${encodeURIComponent(orderId)}/status`, {
            method: "PUT",
            body: JSON.stringify({ status: "CANCELED" }),
          });
        } else {
          // Customer cancel path via dedicated endpoint using sessionId
          let sid = null;
          try {
            sid =
              typeof localStorage !== "undefined" ? localStorage.getItem("qm_order_session") : null;
          } catch {
            /* ignore */
          }
          if (sid) {
            updated = await fetchJson(`/api/orders/${encodeURIComponent(orderId)}/cancel`, {
              method: "POST",
              body: JSON.stringify({ sessionId: sid }),
            });
          } else {
            // No session available; fallback to staff endpoint
            updated = await fetchJson(`/api/orders/${encodeURIComponent(orderId)}/status`, {
              method: "PUT",
              body: JSON.stringify({ status: "CANCELED" }),
            });
          }
        }
      } else {
        const payload = { status: normalized.toUpperCase() };
        updated = await fetchJson(`/api/orders/${encodeURIComponent(orderId)}/status`, {
          method: "PUT",
          body: JSON.stringify(payload),
        });
      }
      const serverStatus = String(updated?.status || normalized).toLowerCase();
      const o2 = orders.value.find((x) => x.id === orderId);
      if (o2) o2.status = serverStatus;
      if (ordersById.value[key]) ordersById.value[key].status = serverStatus;
      if (serverStatus === "payment_completed") {
        // Only clear session if no other active orders remain in this session
        try {
          const sid =
            typeof localStorage !== "undefined" ? localStorage.getItem("qm_order_session") : null;
          if (sid) {
            const r = await apiFetch("/api/orders/session/" + encodeURIComponent(sid));
            if (r.status === 410) {
              // server says expired
              try {
                localStorage.removeItem("qm_order_session");
                localStorage.removeItem("qm_order_session_expires");
                localStorage.removeItem("qm_order_session_tenant");
                deleteCookie("qm_order_session");
              } catch {
                /* ignore */
              }
            } else if (r.ok) {
              const data = await r.json();
              const hasActive =
                Array.isArray(data) &&
                data.some((o) => String(o.status || "").toLowerCase() !== "payment_completed");
              if (!hasActive) {
                try {
                  localStorage.removeItem("qm_order_session");
                  localStorage.removeItem("qm_order_session_expires");
                  localStorage.removeItem("qm_order_session_tenant");
                  deleteCookie("qm_order_session");
                } catch {
                  /* ignore */
                }
              }
            }
          }
        } catch {
          /* ignore */
        }
      }
    } catch {
      // revert UI on failure
      const prev = ordersById.value[key]?.status || o?.status || "new";
      if (o) o.status = prev;
      if (ordersById.value[key]) ordersById.value[key].status = prev;
      // error toast already shown by fetchJson
      throw new Error("status update failed");
    }
  }

  function getOrderById(id) {
    const key = String(id);
    return ordersById.value[key] || orders.value.find((o) => String(o.id) === key);
  }

  // Clear sensitive state on logout/role switch
  function resetAfterLogout() {
    orders.value = [];
    ordersById.value = {};
  }

  return {
    menu,
    menuLoaded,
    cart,
    orders,
    ordersById,
    loadMenu,
    loadOrders,
    refreshMenu,
    clearMenuCache,
    loadSessionOrders,
    addToCart,
    removeFromCart,
    clearCart,
    createOrder,
    updateOrderStatus,
    getOrderById,
    resetAfterLogout,
  };
});
