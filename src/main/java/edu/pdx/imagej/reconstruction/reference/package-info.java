/** This plugin acquires and applies a reference hologram to remove phase noise.
 * There are many different ways to get a reference hologram, with the defaults
 * being {@link Single}, {@link Offset}, {@link Median}, {@link MedianOffset},
 * and {@link Self}.  You may make your own by making a {@link ReferencePlugin}.
 */
package edu.pdx.imagej.reconstruction.reference;
