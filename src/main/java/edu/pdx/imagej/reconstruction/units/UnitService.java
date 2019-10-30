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

import net.imagej.ImageJService;

/** A service to remember what units are used for what inputs.
 */
public interface UnitService extends ImageJService {
    /** Get the units used for wavelength.
     * @return The units used for wavelength.
     */
    DistanceUnits wavelength();
    /** Get the units used for image dimensions.
     * @return The units used for image dimensions.
     */
    DistanceUnits image();
    /** Get the units used for z.
     * @return The units used for z.
     */
    DistanceUnits z();

    /** Set the units used for wavelength.
     * @param val The unit to set it to.
     */
    void setWavelength(String val);
    /** Set the units used for image dimensions.
     * @param val The unit to set it to.
     */
    void setImage(String val);
    /** Set the units used for z.
     * @param val The unit to set it to.
     */
    void setZ(String val);
}
