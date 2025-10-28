<template>
  <div class="relative inline-block w-full" @keydown.stop.prevent="onKeydown">
    <button
      type="button"
      :aria-expanded="open"
      @click.stop="toggle"
      ref="btn"
      tabindex="0"
      class="w-full text-left bg-white border rounded-lg shadow-sm px-4 py-2 flex items-center justify-between focus:outline-none focus:ring-2 focus:ring-brand-200"
    >
      <span class="truncate">{{ selectedLabel }}</span>
      <svg
        class="w-5 h-5 text-gray-400 ml-2"
        viewBox="0 0 20 20"
        fill="none"
        stroke="currentColor"
        stroke-width="1.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M6 8l4 4 4-4" />
      </svg>
    </button>

    <transition name="fade">
      <teleport to="body">
        <ul
          v-if="open"
          ref="list"
          :style="listStyle"
          class="z-50 bg-white border rounded-lg shadow-lg max-h-64 overflow-auto"
        >
          <li
            v-for="(opt, idx) in options"
            :key="opt.value"
            :class="[
              'px-4 py-2 cursor-pointer',
              idx === highlight ? 'bg-brand-50' : 'hover:bg-gray-50',
            ]"
            @mousedown.prevent="select(opt.value)"
            @mouseenter="highlight = idx"
          >
            {{ opt.label }}
          </li>
        </ul>
      </teleport>
    </transition>
  </div>
</template>

<script>
  import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from "vue";

  export default {
    name: "BaseSelect",
    props: {
      modelValue: { required: true },
      options: { type: Array, default: () => [] },
    },
    emits: ["update:modelValue"],
    setup(props, { emit }) {
      const open = ref(false);
      const highlight = ref(0);
      const btn = ref(null);
      const list = ref(null);
      const listStyle = ref({});

      const selectedLabel = computed(() => {
        const sel = props.options.find((o) => o.value === props.modelValue);
        return sel ? sel.label : "";
      });

      function toggle() {
        open.value = !open.value;
        if (open.value) positionList();
      }
      function close() {
        open.value = false;
      }
      function select(v) {
        emit("update:modelValue", v);
        close();
      }

      async function positionList() {
        // wait for DOM to update (teleport/transition) before measuring
        await nextTick();
        if (!btn.value) return;
        const rect = btn.value.getBoundingClientRect();
        const top = rect.bottom + window.scrollY + 6;
        const left = rect.left + window.scrollX;
        const width = rect.width;
        listStyle.value = {
          position: "absolute",
          top: top + "px",
          left: left + "px",
          width: width + "px",
        };
      }

      function onKeydown(e) {
        if (!open.value && (e.key === "Enter" || e.key === " ")) {
          open.value = true;
          return;
        }
        if (e.key === "Escape") return close();
        if (e.key === "ArrowDown") {
          highlight.value = Math.min(highlight.value + 1, props.options.length - 1);
          return;
        }
        if (e.key === "ArrowUp") {
          highlight.value = Math.max(highlight.value - 1, 0);
          return;
        }
        if (e.key === "Enter") {
          const opt = props.options[highlight.value];
          if (opt) select(opt.value);
        }
      }

      function onDocumentClick(e) {
        if (!btn.value) return;
        if (btn.value.contains(e.target)) return;
        if (list.value && list.value.contains && list.value.contains(e.target)) return;
        close();
      }

      watch(
        () => props.options,
        () => {
          if (props.options.length === 0) highlight.value = 0;
        },
      );

      onMounted(() => {
        // ensure highlight aligns with selected
        const idx = props.options.findIndex((o) => o.value === props.modelValue);
        if (idx >= 0) highlight.value = idx;
        window.addEventListener("resize", positionList);
        window.addEventListener("scroll", positionList, true);
        document.addEventListener("click", onDocumentClick);
      });

      watch(open, (v) => {
        if (v) positionList();
      });

      onBeforeUnmount(() => {
        window.removeEventListener("resize", positionList);
        window.removeEventListener("scroll", positionList, true);
        document.removeEventListener("click", onDocumentClick);
      });

      return { open, toggle, select, highlight, selectedLabel, btn, list, onKeydown, listStyle };
    },
  };
</script>

<style scoped>
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.15s;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
</style>
