#include "ffi/ffi.h"

#include "ffi/prep_cif.c"
#include "ffi/ffi.c"


//#include <dlfcn.h>


static PyObject *
embed_log(PyObject * self, PyObject * args) {
    char *logstr = NULL;
    if (!PyArg_ParseTuple(args, "s", &logstr)) {
        return NULL;
    }
    int rx = EM_ASM_INT({ return Module.printErr(Pointer_stringify($0));
                        }
                        , logstr);
    Py_RETURN_NONE;
}

static PyObject *
embed_select(PyObject * self, PyObject * args) {
    int fdnum = -1;
    if (!PyArg_ParseTuple(args, "i", &fdnum)) {
        return NULL;
    }
    return Py_BuildValue("i", EM_ASM_INT( {
                                         return Module.has_io($0);
                                         }
                                         , fdnum));
}


static PyObject *
embed_exit(PyObject * self, PyObject * args) {
    int ec = 1;
    if (!PyArg_ParseTuple(args, "i", &ec)) {
        return NULL;
    }
    emscripten_force_exit(ec);
    Py_RETURN_NONE;
}

PyMODINIT_FUNC PyInit_core(void);

PyMODINIT_FUNC PyInit_direct(void);




static PyMethodDef embed_funcs[] = {
    {"log", embed_log, METH_VARARGS, "Log on browser console only"},
    {"select", embed_select, METH_VARARGS, "select on non blocking io stream"},
    //{"panda3d", embed_panda3d, METH_VARARGS, "p3d"},
    {"exit", embed_exit, METH_VARARGS, "exit emscripten"},
    {NULL, NULL, 0, NULL}
};


static struct PyModuleDef embed = { PyModuleDef_HEAD_INIT, "embed", NULL, -1, embed_funcs };

static PyObject *embed_dict;


PyMODINIT_FUNC
embed_init(void) {
    PyObject *embed_mod;
    embed_mod = PyModule_Create(&embed);
    embed_dict = PyModule_GetDict(embed_mod);
    PyDict_SetItemString(embed_dict, "js2py", PyUnicode_FromString("{}"));
    return embed_mod;
}


































