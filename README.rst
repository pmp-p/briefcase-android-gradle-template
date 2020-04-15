(Future Briefcase) Android Gradle Template
=================================

A `Cookiecutter <https://github.com/cookiecutter/cookiecutter/>`__ template for
building Python apps that will run under Android.

The easiest way to use this project is to not use it at all - at least, not
directly. `Briefcase <https://github.com/beeware/briefcase/>`__ is a tool that
probably could uses this template (but not yet), rolling it out using data extracted from a
``pyproject.toml`` configuration file.

This version is targeting PyDK prebuilt by default including Panda3D, not beeware android pipeline.
===================================================================================================

The master branch of this repository has no content; there is an independent
branch for each supported version of Python. The following Python versions are
supported:

* `Python 3.8 PyDK <https://github.com/pmp-p/briefcase-android-gradle-template/tree/3.8p>`__


usage:

PYDK=/path/to/build/of/pydk ./new.sh


Suggestions for template changes are welcomed by :

pmp-p on irc.freenode.net via #h3droid / #python-fr / #micropython-fr

template loading only bytecode instead of clear Python code can be discussed.
