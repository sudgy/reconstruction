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

package edu.pdx.imagej.reconstruction.units;

/** An enumeration representing different length units.
 */
public enum DistanceUnits {
    Nano, Micro, Milli, Centi, Meter;

    /** Convert a double value from one unit to another.
     *
     * @param value The numerical value of the input.
     * @param from The units for the input.
     * @param to The units for the output.
     * @return The numerical value of the output.
     */
    public static double convert(double value, DistanceUnits from,
                                 DistanceUnits to)
    {
        double mult = 1;
        switch (from) {
            case Nano: mult *= 1e-9; break;
            case Micro: mult *= 1e-6; break;
            case Milli: mult *= 1e-3; break;
            case Centi: mult *= 1e-2; break;
            case Meter: break;
        }
        switch (to) {
            case Nano: mult *= 1e9; break;
            case Micro: mult *= 1e6; break;
            case Milli: mult *= 1e3; break;
            case Centi: mult *= 1e2; break;
            case Meter: break;
        }
        return value * mult;
    }

    /** Convert a string to a DistanceUnits.  Note that this is not intended to
     * replace <code>valueOf</code>, as this reads string like "Nanometers" when
     * <code>valueOf</code> just reads strings like "Nano".
     *
     * @param s The string to read from.  It needs to be something like
     *          "Millimeters", and in the case of <code>Meter</code>, it is just
     *          "Meters".
     * @return The unit that corresponds with the string.
     */
    public static DistanceUnits value_of(String s)
    {
        if (s == null) return null;
        switch (s) {
            case "Nanometers": return Nano;
            case "Micrometers": return Micro;
            case "Millimeters": return Milli;
            case "Centimeters": return Centi;
            case "Meters": return Meter;
        }
        return null;
    }
    /** Convert this to a string, using abbreviations.  Because it uses
     * abbreviations, <code>value_of(unit.toString())</code> will return <code>
     * null</code>, not the unit again.
     *
     * @return The abbreviation for this unit.
     */
    @Override
    public String toString()
    {
        switch (this) {
            case Nano: return "nm";
            case Micro: return "µm";
            case Milli: return "mm";
            case Centi: return "cm";
            case Meter: return "m";
        }
        return null;
    }
}
