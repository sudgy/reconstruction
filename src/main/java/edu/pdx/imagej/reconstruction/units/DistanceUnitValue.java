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

/** A numerical value associated with a unit.  The only way to access the
 * numerical value is to say what unit you want the value to be in.  This
 * ensures that units will never be forgotten.
 */
public class DistanceUnitValue {
    /** Construct a <code>DistanceUnitValue</code> with a value of zero. */
    public DistanceUnitValue()
    {
        M_value = 0;
        M_unit = DistanceUnits.Meter;
    }
    /** Construct a <code>DistanceUnitValue</code> using a numerical value and
     * its units.
     *
     * @param value The numerical value to use.
     * @param unit The units for the numerical value.
     */
    public DistanceUnitValue(double value, DistanceUnits unit)
    {
        M_value = value;
        M_unit = unit;
    }
    /** Get the value in the same units as when it was constructed.  <strong>You
     * should not use this when actually doing calculations!</strong>  Instead
     * you should use one of the <code>as_*</code> methods.  This should only be
     * used when displaying the value to the user in the units he remembers
     * putting them in.
     *
     * @return The numerical value used to construct this value.
     */
    public double value() {return M_value;}
    /** Get the unit used to construct this with.
     *
     * @return The {@link DistanceUnits} used to construct this value.
     */
    public DistanceUnits unit() {return M_unit;}
    /** Get the numerical value of this value in nanometers.
     *
     * @return The numerical value in nanometers.
     */
    public double asNano()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Nano);
    }
    /** Get the numerical value of this value in micrometers.
     *
     * @return The numerical value in micrometers.
     */
    public double asMicro()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Micro);
    }
    /** Get the numerical value of this value in millimeters.
     *
     * @return The numerical value in millimeters.
     */
    public double asMilli()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Milli);
    }
    /** Get the numerical value of this value in centimeters.
     *
     * @return The numerical value in centimeters.
     */
    public double asCenti()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Centi);
    }
    /** Get the numerical value of this value in meters.
     *
     * @return The numerical value in meters.
     */
    public double asMeter()
    {
        return DistanceUnits.convert(M_value, M_unit, DistanceUnits.Meter);
    }
    private double M_value;
    private DistanceUnits M_unit;
}
