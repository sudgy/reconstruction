/** This plugin performs tilt correction on the filtered fields using a
 * polynomial fit algorithm.  It picks lines that should be flat on the image,
 * finds a polynomial fit to it, then subtracts that polynomial from the phase.
 * Flat line determination is nontrivial, so there are several different methods
 * to determine these lines.  Currently, the default line determination methods
 * are {@link Auto}, {@link Middle}, and {@link Manual}, and you can create your
 * own by making a {@link PolyTiltPlugin}.
 */
package edu.pdx.imagej.reconstruction.poly_tilt;
