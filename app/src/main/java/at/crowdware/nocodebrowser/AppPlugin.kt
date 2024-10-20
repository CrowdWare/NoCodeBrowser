package at.crowdware.nocodebrowser

import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo

/**
 * Runtime [GodotPlugin] used to enable interaction with the Godot gdscript logic.
 */
class AppPlugin(godot: Godot) : GodotPlugin(godot) {

    companion object {
        val SHOW_GLTF_SIGNAL = SignalInfo("show_gltf", String::class.java)
        val LOAD_SCENE_SIGNAL = SignalInfo("load_scene", String::class.java)
    }

    override fun getPluginName() = "AppPlugin"

    override fun getPluginSignals() = setOf(SHOW_GLTF_SIGNAL, LOAD_SCENE_SIGNAL)

    /**
     * Used to emit a signal to the gdscript logic to update the gltf being shown.
     *
     * @param glbFilepath Filepath of the gltf asset to be shown
     */
    internal fun showGLTF(glbFilepath: String) {
        emitSignal(SHOW_GLTF_SIGNAL.name, glbFilepath)
    }

    internal fun loadScene(scenePath: String) {
        println("loadScene $scenePath")
        emitSignal(LOAD_SCENE_SIGNAL.name, scenePath)
    }
}
