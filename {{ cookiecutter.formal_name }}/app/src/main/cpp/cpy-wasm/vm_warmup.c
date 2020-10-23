LOG_I("VMthread starting");


    setenv("PYTHONHOME", "/", 1);
    setenv("PYTHONDONTWRITEBYTECODE", "1", 1);
    setenv("PYTHONINSPECT", "1",0);
    setenv("PYTHONUNBUFFERED","1",1);
    setenv("PYTHONOPTIMIZE", "No",1);
    setenv("PYTHONCOERCECLOCALE", "1", 1);
    setenv("PYTHONUNBUFFERED", "1", 1);



    //Py_SetProgramName((const wchar_t *)"python3.8");

    setlocale(LC_ALL, "C.UTF-8");

    // add our support module
    PyImport_AppendInittab("embed", embed_init);

    snprintf(cstr, sizeof(cstr), "%s/assets", apk_home );

    if (dir_exists(cstr)) {
        snprintf(cstr, sizeof(cstr), "%s/assets/%s", apk_home, python );
        if (dir_exists(cstr)) {
            // test mode use plain files for everything
            LOG_W("!!!!!!!! TESTSUITE MODE !!!!!!!!!!!!");
            snprintf(cstr, sizeof(cstr), "%s/assets/%s:%s/assets", apk_home, python, apk_home );
        } else {
            // dev mode use plain files for not stdlib, and comes first
            LOG_W(" !!!!!!!!!!! DEV MODE !!!!!!!!!!!!");
            //snprintf(cstr, sizeof(cstr), "%s/lib:%s/assets:%s/assets/%s", apk_home, apk_home, apk_path, python);
            snprintf(cstr, sizeof(cstr), "%s/assets:%s/assets:%s/assets/%s:%s/lib", apk_home, apk_path, apk_path, python, apk_home);
        }
    } else
        snprintf(cstr, sizeof(cstr), "%s/assets/%s:%s/assets", apk_path, python, apk_path);



    LOG_V("Setting paths ... ");
    setenv("PYTHONPATH", "/python3.8.zip:/assets/packages:/assets/python3.8:/assets", 1);
    fprintf(stdout,"PYTHONPATH[%s]", getenv("PYTHONPATH") );



//--------------------------------------------
//P1
        LOG_V("Initializing cpython... ");
        Py_Initialize();

        /* ensure threads will work. */
        LOG_V("Initializing cpython threads ...");
        PyEval_InitThreads();

        PyRun_SimpleString("import pythons");

        //do_flush_stdout();


#if __EMSCRIPTEN__
    emscripten_cancel_main_loop();
    emscripten_set_main_loop( main_iteration, 0, 1);
#else
    #pragma message "WASI startup"
#endif


