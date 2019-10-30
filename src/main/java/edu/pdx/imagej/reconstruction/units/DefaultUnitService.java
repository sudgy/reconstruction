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

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;
import org.scijava.service.Service;
import org.scijava.service.AbstractService;

/** The default implementation of {@link UnitService}.
 */
@Plugin(type = Service.class)
public class DefaultUnitService extends AbstractService implements UnitService {
    /** {@inheritDoc} */
    @Override
    public DistanceUnits wavelength()
    {
        return DistanceUnits.valueOf2(P_prefs.get(UnitService.class,
                                                  "wavelength", "Nanometers"));
    }
    /** {@inheritDoc} */
    @Override
    public DistanceUnits image()
    {
        return DistanceUnits.valueOf2(P_prefs.get(UnitService.class, "image",
                                                  "Micrometers"));
    }
    /** {@inheritDoc} */
    @Override
    public DistanceUnits z()
    {
        return DistanceUnits.valueOf2(P_prefs.get(UnitService.class, "z",
                                                  "Micrometers"));
    }

    /** {@inheritDoc} */
    @Override
    public void setWavelength(String val)
    {
        P_prefs.put(UnitService.class, "wavelength", val);
    }
    /** {@inheritDoc} */
    @Override
    public void setImage(String val)
    {
        P_prefs.put(UnitService.class, "image", val);
    }
    /** {@inheritDoc} */
    @Override
    public void setZ(String val)
    {
        P_prefs.put(UnitService.class, "z", val);
    }

    @Parameter private PrefService P_prefs;
}
