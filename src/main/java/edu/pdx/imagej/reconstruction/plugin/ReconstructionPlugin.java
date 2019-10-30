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

package edu.pdx.imagej.reconstruction.plugin;

import java.util.List;

import ij.ImagePlus;

import org.scijava.Prioritized;
import org.scijava.plugin.Plugin;
import net.imagej.ImageJPlugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.ParameterPlugin;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;

/** A plugin that is used for reconstruction.  To extend the functionality of
 * {@link edu.pdx.imagej.reconstruction.ReconstructionOp ReconstructionOp}, you
 * must implement this interface.  In addition, you must implement either {@link
 * MainReconstructionPlugin} or {@link SubReconstructionPlugin} to tell the
 * reconstruction command what kind of plugin this is.  You should almost
 * never implement this interface directly, and should instead extend {@link
 * AbstractReconstructionPlugin}, or maybe {@link AbstractHoldingPlugin}, {@link
 * HoldingPlugin}, or {@link HoldingSinglePlugin} if you require sub plugins in
 * your plugin.
 * <p>
 * The priority of the plugin is used to determine what order to process each
 * enabled plugin at each step.  Because you might want to change the priority
 * for certain steps, you may use the <code>set*Priority</code> methods to
 * change this.  However, if you can use the same priority throughout the entire
 * process, you do not need to override <code>set*Priority</code> at any time.
 * Note that because some of the reconstruction pipeline has loops, you should
 * override {@link setHologramPriority} to reset the priority if you overload
 * any of the later priority methods other than {@link processEnding}.
 * <p>
 * Other than {@link duplicate}, every single one of these methods are
 * defaulted and do nothing.  This allows you to only override what you need,
 * because most of the time you only need to override a few of these methods.
 * <p>
 * All of the following methods are called in this order:
 * <ol>
 *      <li> {@link setBeginningPriority} </li>
 *      <li> {@link readPlugins
 *           readPlugins(List&lt;ReconstructionPlugin&gt;,)} </li>
 *      <li> {@link processBeforeParam} </li>
 *      <li> {@link processHologramParam
 *           processHologramParam(ImagePlus)} </li>
 *      <li> {@link processWavelengthParam
 *           processWavelengthParam(DistanceUnitValue)} </li>
 *      <li> {@link processDimensionsParam
 *           processDimensionsParam(DistanceUnitValue,
 *                                    DistanceUnitValue)} </li>
 *      <li> {@link processTsParam
 *           processTsParam(List&lt;Integer&gt;)} </li>
 *      <li> {@link processZsParam
 *           processZsParam(List&lt;DistanceUnitValue&gt;)} </li>
 *      <li> {@link processBeginning} </li>
 *      <li> {@link setOriginalHologramPriority} </li>
 *      <li> {@link processOriginalHologram
 *           processOriginalHologram(ConstReconstructionField)} </li>
 *      <li> [Loop through time slices] </li>
 *      <li> {@link setHologramPriority} </li>
 *      <li> {@link processHologram
 *           processHologram(ReconstructionField, int)} </li>
 *      <li> {@link setFilteredFieldPriority} </li>
 *      <li> {@link processFilteredField
 *           processFilteredField(ReconstructionField, int)} </li>
 *      <li> {@link setPropagatedFieldPriority} </li>
 *      <li> [Loop through z slices] </li>
 *      <li> {@link processPropagatedField
 *           processPropagatedField(ReconstructionField,
 *                                    int,
 *                                    DistanceUnitValue)} </li>
 *      <li> [End loop through z slices] </li>
 *      <li> [End loop through time slices] </li>
 *      <li> {@link setEndingPriority} </li>
 *      <li> {@link processEnding} </li>
 * </ol>
 * While it is guaranteed that each method will be called after all of the
 * previous ones, plugins are allowed to call earlier methods again if they
 * choose to.  For example, {@link
 * edu.pdx.imagej.reconstruction.auto_focus.AutoFocus AutoFocus} calls {@link
 * processZsParam processZsParam} several times.  In addition, if {@link
 * hasError} returns <code>true</code> at any time, the command will abort.
 */
public interface ReconstructionPlugin extends ImageJPlugin, ParameterPlugin {
    /** Read the all of the main reconstruction plugins enabled.  Some plugins
     * use other plugins as a part of their functionality.  As an example,
     * {@link edu.pdx.imagej.reconstruction.reference.Reference Reference} uses
     * this to get the {@link edu.pdx.imagej.reconstruction.filter.Filter
     * Filter}.
     *
     * @param plugins All enabled plugins.
     */
    default void readPlugins(List<ReconstructionPlugin> plugins) {}

    /** Perform any setup before acquiring the basic parameters.
     */
    default void processBeforeParam() {}
    /** Perform any setup with the original hologram.
     *
     * @param hologram The hologram being reconstructed.
     */
    default void processHologramParam(ImagePlus hologram) {}
    /** Perform any setup with the wavelength.
     *
     * @param wavelength The wavelength the user specified when running the
     *                   command.
     */
    default void processWavelengthParam(DistanceUnitValue wavelength) {}
    /** Perform any setup with the dimensions of the hologram.
     *
     * @param width The width of the hologram in real units.
     * @param height The height of the hologram in real units.
     */
    default void processDimensionsParam(DistanceUnitValue width,
                                          DistanceUnitValue height) {}
    /** Perform any setup with the times that will be used to reconstruct.  Note
     * that when propagating, you will be notified of what particular time slice
     * is being reconstructed.  This should only be used if you need all of the
     * time information at once.
     *
     * @param ts A list of integers representing the time slices used to
     *           reconstruct with.
     */
    default void processTsParam(List<Integer> ts) {}
    /** Perform any setup with the z slices that will be propagated to.  Like
     * {@link processTsParam}, you will be notified of particular z slices
     * during propagation, and should only use this if you need all of the z
     * information at once.
     *
     * @param zs A list of {@link DistanceUnitValue}s that will be propagated
     * to.
     */
    default void processZsParam(List<DistanceUnitValue> zs) {}

    /** Perform any setup before starting the actual reconstruction.
     */
    default void processBeginning() {}

    /** Perform any calculations wanted on the original hologram, before doing
     * any reconstruction at all.
     *
     * @param field The original hologram field.
     */
    default void processOriginalHologram(ConstReconstructionField field) {}
    /** Perform any calculations wanted on each hologram before they are
     * filtered.
     *
     * @param field The current hologram field.
     * @param t The t slice that was used to get this hologram.
     */
    default void processHologram(ReconstructionField field, int t) {}
    /** Perform any calculations wanted on the filtered field, before
     * propagation.  This is a good time to deal with issues such as noise.
     *
     * @param field The Filtered field, before propagation.
     * @param t The t slice that was used to get this field.
     */
    default void processFilteredField(ReconstructionField field, int t) {}
    /** Perform any calculations wanted on the propagated field.
     *
     * @param field The current, propagated field.
     * @param t The t slice that was used to get this field.
     * @param z The z value that was used to get this field.
     */
    default void processPropagatedField(ReconstructionField field,
                                          int t, DistanceUnitValue z) {}
    /** Perform any processing wanted after everything is finished.
     */
    default void processEnding() {}

    /** Set the priority of this plugin before anything happens.
     */
    default void setBeginningPriority()         {}
    /** Set the priority of this plugin before the calculations actually start.
     */
    default void setOriginalHologramPriority() {}
    /** Set the priority of this plugin before processing each hologram.
     */
    default void setHologramPriority()          {}
    /** Set the priority of this plugin before processing the filtered fields.
     */
    default void setFilteredFieldPriority()    {}
    /** Set the priority of this plugin before processing the propagated fields.
     */
    default void setPropagatedFieldPriority()  {}
    /** Set the priority of this plugin at the end.
     */
    default void setEndingPriority()            {}

    /** Whether or not the command should cancel because an error has occurred.
     *
     * @return Whether or not there is an error.
     */
    default boolean hasError() {return false;}

    /** All of the dependencies that this plugin has on other plugins.  If you
     * have a dependency, it will not be disabled unless this plugin is disabled
     * as well.
     *
     * @return A <code>List</code> of all dependencies.
     */
    default List<Class<? extends ReconstructionPlugin>> dependencies()
        {return null;}
    /** All of the sub plugin <em>types</em> this plugin uses.  You should only
     * return the top-level types, not any particular sub plugin.  Almost
     * always, when you override this method, you will just return one thing,
     * but in case you do something wacky there is room for more here.
     *
     * @return A <code>List</code> of the sub plugin types that this plugin
     *         uses.
     */
    default List<Class<? extends ReconstructionPlugin>> subPlugins()
        {return null;}
    /** A dynamic parameter that will be used in the options command.  If this
     * returns <code>null</code>, this plugin will have no options other than
     * enabling/disabling.
     *
     * @return a <code>DParameter</code> that will get the options needed for
     *         this plugin, or <code>null</code> if no options are needed for
     *         this plugin.
     */
    default DParameter<?> optionsParam() {return null;}
    /** Read the options from the options parameter.  This method is called
     * after the options command is run, at which point {@link optionsParam}
     * should contain all of the necessary values.
     */
    default void readOptions() {}
    /** Get the name of this parameter.  It will return whatever the name was
     * set to in <code>@Plugin</code>, so you should never need to override
     * this.
     *
     * @return The name of this plugin.
     */
    default String getName()
    {
        String result = null;
        Plugin annotation = getClass().getAnnotation(Plugin.class);
        if (annotation != null) result = annotation.name();
        if (result == null || result.equals("")) {
            result = getClass().getName();
        }
        return result;
    }
    /** Duplicate the plugin, reseting the state of the duplicated plugin.
     * This is used so that {@link
     * edu.pdx.imagej.reconstruction.ReconstructionOp ReconstructionOp} can be
     * run several times with the same (or almost the same) options.  This
     * usually means that either a deep copy is required or you need to just
     * recreate the plugin from scratch, but as long as you can ensure that
     * separate runs won't interfere with each other, do whatever you want.
     * Note that like <code>clone</code>, <code>this.getClass()</code> must be
     * equal to <code>duplicate().getClass()</code>.
     *
     * @return An effectively deep copy of this plugin, with a reset state
     */
    ReconstructionPlugin duplicate();
}
