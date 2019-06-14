/** This package has all classes relating to plugins for reconstruction and their
 * parameters.  This package is kind of messy, but hopefully this guide here
 * should help you figure out what you need to use.
 * <p>
 * In general, all plugins should somehow inherit {@link ReconstructionPlugin}.
 * In addition, all plugins <strong>must</strong> implement one of {@link
 * MainReconstructionPlugin} or {@link SubReconstructionPlugin}, or else the
 * reconstruction command will give an error.  A main plugin is one that is
 * directly used by the command, and a sub plugin is one that will not be used
 * by the command directly.  For an example of this split, {@link
 * edu.pdx.imagej.reconstruction.reference.Reference Reference} is a main
 * plugin, while {@link edu.pdx.imagej.reconstruction.reference.ReferencePlugin
 * ReferencePlugin} is a sub plugin.
 * <p>
 * When creating a new plugin class, they should all be annotated with
 * <code>@Plugin</code> with
 * <code>type = {@link ReconstructionPlugin}.class</code>.  You may also want
 * to set the priority as well, and the higer priority plugins will be run first
 * at each step of the process.  The name should be set to something sensible,
 * as it is used in various places.  However, what classes/interfaces to
 * extend/implement is more confusing:
 * <ul>
 *      <li>
 *          If you are making a main plugin, extend {@link
 *          AbstractReconstructionPlugin} and implement {@link
 *          MainReconstructionPlugin}.  As an example, {@link
 *          edu.pdx.imagej.reconstruction.filter.Filter Filter} is a plugin that
 *          does this.
 *      </li>
 *      <li>
 *          If you are making a main plugin that has an option to choose from
 *          additional plugins, extend {@link HoldingSinglePlugin} and implement
 *          {@link MainReconstructionPlugin}.  As an example, {@link
 *          edu.pdx.imagej.reconstruction.reference.Reference Reference} is a
 *          plugin that does this.
 *      </li>
 *      <li>
 *          If you are making the interface of a sub plugin, do not use
 *          <code>@Plugin</code> and just extend {@link
 *          SubReconstructionPlugin}. You should also make an abstract class
 *          that implements your interface and extends
 *          <code>AbstractRichPlugin</code>.  As an example, {@link
 *          edu.pdx.imagej.reconstruction.reference.ReferencePlugin
 *          ReferencePlugin}/{@link
 *          edu.pdx.imagej.reconstruction.reference.AbstractReferencePlugin
 *          AbstractReferencePlugin} is a plugin type that does this.
 *      </li>
 *      <li>
 *          If you are making a sub plugin, your <code>@Plugin</code> type
 *          should instead be the type of the interface that that plugin is, and
 *          you should just extend the abstract class.  As an example, {@link
 *          edu.pdx.imagej.reconstruction.reference.Single reference.Single} is
 *          a plugin that does this.
 *      </li>
 * </ul>
 * <p>
 * When creating a new parameter, you can make it whatever kind of parameter you
 * want.  However, there are still a few things that you may want to use/watch
 * out for:
 * <ul>
 *      <li>
 *          If you want to see the hologram that is being selected, implement
 *          {@link HologramPluginParameter} as well, and then you will be
 *          notified whenever a hologram is selected.  As an example, {@link
 *          edu.pdx.imagej.reconstruction.reference.Single.SingleParameter
 *          reference.Single.SingleParameter} is a parameter that does this.
 *      </li>
 *      <li>
 *          If you want to use HoldingParameter from dynamic_parameters, you
 *          should instead use {@link ReconstructionHoldingParameter}.  This is
 *          used to make {@link HologramPluginParameter} still work.
 *      </li>
 *      <li>
 *          If you want to use PluginParameter from dynamic_parameters, if it is
 *          for a {@link ReconstructionPlugin}, you should instead use {@link
 *          ReconstructionPluginParameter} to make {@link
 *          HologramPluginParameter} work.  Otherwise, you should probably make
 *          it a {@link HologramPluginParameter} itself and pass the hologram to
 *          the plugin parameter yourself.
 *      </li>
 *      <li>
 *          If you are using {@link HoldingSinglePlugin}, you already have a
 *          {@link ReconstructionPluginParameter} for your plugin.  If you want
 *          to have additional parameters, make a {@link
 *          ReconstructionHoldingParameter} that holds your plugin parameter
 *          along with everything else you want.  As an example, {@link
 *          edu.pdx.imagej.reconstruction.reference.Reference Reference}/{@link
 *          edu.pdx.imagej.reconstruction.reference.ReferenceParameter
 *          ReferenceParameter} do this.
 *      </li>
 * </ul>
 * It is still possible that you might want to do something else.  If you do,
 * extend whatever you wish, just remember to keep passing along all methods
 * from your top-level plugins/parameters to your lowest level ones.
 */
package edu.pdx.imagej.reconstruction.plugin;
