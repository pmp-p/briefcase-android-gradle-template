#if __EMSCRIPTEN__
    #include "cpy-wasm/cpython_wasm.c"
#else
    #include "cpy-aosp/cpython_aosp.c"
#endif
