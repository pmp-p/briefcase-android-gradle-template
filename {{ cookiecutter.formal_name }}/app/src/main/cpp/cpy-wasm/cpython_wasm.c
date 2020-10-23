#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <assert.h>
#include <stdarg.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>


#include "wasx.h"



int dir_exists(char *filename) {
    struct stat st;
    if (stat(filename, &st) == 0) {
        if (S_ISDIR(st.st_mode))
          return 1;
        }
    return 0;
}

int file_exists(const char *filename) {
    FILE *file = fopen(filename, "r") ;
    if (file) {
        fclose(file);
        return 1;
    }
    return 0;
}

#include "Python.h"


char * cstr;




#define LOG_TAG "wasm[%s]\n"

#define LOG_V(data) puts(data)
#define LOG_W(data) puts(data)
#define LOG_I(data) puts(data)

#define LOG(fmt, data) fprintf(stderr, fmt, data)


#define setlocale(...)


#define __MAIN__ (1)
#include "emscripten.h"
#undef __MAIN__


#include "modcpy_emded.c"





void
main_iteration(void) {
    #include "vm_loop.c"
}




// python version lib name, to use directly python3.? folders found in prefix/lib
#define python "python3.8"

// stdlib archive path (apk==zip)
#define apk_path "/pyweb.zip"

// work directory will chdir here.
#define apk_home "/"

#define apk_lib "/lib"


void
main_warmup(void) {
   #include "vm_warmup.c"
}


int
main(int argc, char *argv[]) {
    setbuf(stdout, NULL);
    //setvbuf (stdout, NULL, _IONBF, BUFSIZ);
    //fflush(NULL)

    printf("Press ctrl+shift+i to see debug logs, or go to Menu / [more tools] / [developpers tools]\r\n");

    cstr = malloc(4096);

    // first go somewhere writeable !
    chdir(apk_home);


    if (!mkdir("dev", 0700)) {
       LOG_V("no 'dev' directory, creating one ...");
    }


    if (!mkdir("tmp", 0700)) {
       LOG_V("no 'tmp' directory, creating one ...");
    }

    setenv("XDG_CONFIG_HOME", apk_home, 1);
    setenv("XDG_CACHE_HOME", apk_home, 1);



/*
    snprintf(cstr, sizeof(cstr), "%s/tmp", apk_home );
    setenv("TEMP", cstr, 0);
    setenv("TMP", cstr, 0);
*/


    // potentially all apps signed from a same editor could have same UID  ( shared-uid )
    // though different apk names.


/*
    // be a bit more nix friendly
    setenv("HOME", apk_home, 1);

    setenv("DYLD", apk_lib, 1 );

   setenv("USER", LOG_TAG, 1);
    snprintf(cstr, sizeof(cstr), "%s", apk_home );
    char* token = strtok(cstr, "/");
    while (token != NULL) {
        setenv("USERNAME", token, 1);
        token = strtok(NULL, "/");
    }
*/
    // TODO: pip binary modules
    // TODO: PYTHONPYCACHEPREFIX
    //setenv("PYTHONHOME", apk_home + "/usr", 1);

    //dlopen("dl", RTLD_NOW);
    //dlopen(LIB_PYTHON, RTLD_NOW);

    emscripten_set_main_loop(main_warmup, 0, 1);     // <= this will exit to js now.

    return 0;                   // success
}






