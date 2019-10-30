/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.reconstruction.result;

import java.util.function.Consumer;

import ij.ImagePlus;

/** The options to use for {@link Result}.  This is just a bunch of data with no
 * OOP semantics.
 */
public class ResultOptions {
    /** Whether or not to show the amplitude of the result. */
    public boolean amplitude;
    /** Whether or not to show the phase of the result. */
    public boolean phase;
    /** Whether or not to show the real part of the result. */
    public boolean real;
    /** Whether or not to show the imaginary part of the result. */
    public boolean imaginary;
    /** Enumeration specifying what type of image to output. */
    public enum Type {Type8Bit, Type16Bit, Type32Bit};
    /** The type of image to output. */
    public Type type;
    /** Whether you should save the result to a file, or use a user-supplied
     * function with the result.
     */
    public boolean saveToFile;
    /** What directory to save to, if {@link saveToFile} is <code>true</code>.
     */
    public String saveDirectory;
    /** Function to call for the amplitude result if {@link saveToFile} is
     * <code>false</code>.  It defaults to showing the ImagePlus.
     */
    public Consumer<ImagePlus> amplitudeFunc = (ImagePlus imp) -> imp.show();
    /** Function to call for the phase result if {@link saveToFile} is
     * <code>false</code>.  It defaults to showing the ImagePlus.
     */
    public Consumer<ImagePlus> phaseFunc = (ImagePlus imp) -> imp.show();
    /** Function to call for the real result if {@link saveToFile} is
     * <code>false</code>.  It defaults to showing the ImagePlus.
     */
    public Consumer<ImagePlus> realFunc = (ImagePlus imp) -> imp.show();
    /** Function to call for the imaginary result if {@link saveToFile} is
     * <code>false</code>.  It defaults to showing the ImagePlus.
     */
    public Consumer<ImagePlus> imaginaryFunc = (ImagePlus imp) -> imp.show();
}
