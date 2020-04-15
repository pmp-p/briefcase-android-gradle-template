#!/bin/sh
export PYTHONDONTWRITEBYTECODE=1

PREFIX=$(realpath $(dirname "$0"))

if echo $PYDK|grep -q pydk
then
    echo " * Using HOST python from PyDK build"
    HOST=$(echo -n ${PYDK}/host)
    echo HOST=$HOST

    PYTHON=$(echo -n ${HOST}/bin/python3.?)
    PIP=$(echo -n ${HOST}/bin/pip3.?)
    export LD_LIBRARY_PATH="${HOST}/lib64:${HOST}/lib:$LD_LIBRARY_PATH"

else
    echo " * Using non PyDK-sdk cPython3 ( like beeware-venv )"
    export PYTHON=$(command -v python3)
    export PIP=$(command -v pip3)
fi

export PATH=$(dirname $PYTHON):$PATH
echo PATH=$(dirname $PYTHON)
echo PYTHON=$PYTHON
echo PIP=$PIP


$PIP install --upgrade pip
$PIP install briefcase

export COOKIECUTTER_CONFIG=cookiecutter.config
mkdir templates replay

cat > $COOKIECUTTER_CONFIG <<END
cookiecutters_dir: templates
replay_dir: replay
END


cat > templates/briefcase-android-gradle-template/cookiecutter.json <<END
{
  "module_name": "empty",
  "bundle": "org.beerware",
  "app_name": "EmptyApp",
  "formal_name": "org.beerware.empty",
  "_copy_without_render": [
    "gradlew",
    "gradle.bat",
    "gradle/wrapper/gradle-wrapper.properties",
    "gradle/wrapper/gradle-wrapper.jar",
    ".gitignore",
    "*.png"
  ]
}
END

cookiecutter https://github.com/pmp-p/briefcase-android-gradle-template --checkout 3.8p
