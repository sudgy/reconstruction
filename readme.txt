Copyright (C) 2019 Portland State University
Hologram Reconstruction and Numerical Propagation for ImageJ2 - David Cohoe

Reconstruction is a plugin for ImageJ2 that performs reconstruction on holograms
and numerical propagation on the resulting complex fields.  It is highly
extendable, with the ability to insert plugins at any step during the process.
A few plugins are included by default to perform the propagation as well as a
few additional features such as noise removal.

INSTALLATION

To install the plugin, the update site "DHM Utilities" with the URL
"http://sites.imagej.net/Sudgy/" must be added in the ImageJ updater.  If you
want to modify the plugin, or if you want to install the plugin without
everything else from DHM utilities, compile it with maven and then copy the jar
to the ImageJ plugins folder, removing the old one if you need to.  This plugin
depends on dynamic_parameters, another plugin in DHM utilities, which can be
found at https://github.com/sudgy/dynamic-parameters.  The documentation can be
created using maven's javadoc plugin, and will be created in
target/site/apidocs/.

USE

To use the plugin, run the command "Plugins > DHM Utilities > Reconstruction".
A fairly-large dialog will greet you with all of the required parameters.  The
plugin will then use all enabled plugins and perform reconstruction and
propagation, displaying the results if requested.

Here is a list of all of the default plugins:
  - Filter: Performs filtering of the holograms and crude centering.  This
            should always be on.
  - Propagate: Performs propagation of the filtered field.  This should probably
               always be on.
  - Result: Displays the results of the propagation either to display or to
            files.  This should always be on.
  - Status: Displays the status/progress during the calculations.
  - Reference: Retrieves and applies a reference hologram to the filtered
               fields.
  - Poly Tilt: Attempts to remove tilt errors by using a polynomial fit
               algorithm.

Options for the plugins can be changed using the "Plugins > DHM Utilities >
Reconstruction Options" command.  New plugins may opt in to having their options
in this command as well.

For more specifics on any of the algorithms and plugins, please consult the
documentation.


If you have any questions that are not answered here, in the documentation, or
in the source code, please email David Cohoe at dcohoe@pdx.edu.
