package co.edu.unicauca.bancopreguntas.microkernel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Registro y carga dinamica de plugins mediante reflexion, tal como lo pide
 * la guia de la actividad ("del ejemplo del envio de paquetes se puede tomar
 * el codigo para el registro y ejecucion dinamica de plugins basado en
 * Reflexion"). El archivo {@code plugins.properties} declara, en el
 * classpath, pares {@code nombre-logico=FQCN-de-la-clase-del-plugin}; esta
 * clase los instancia con {@link Class#forName(String)} y un constructor sin
 * argumentos, sin que el nucleo dependa en tiempo de compilacion de ninguna
 * clase concreta de plugin.
 */
public final class PluginLoader {

    private PluginLoader() {
    }

    /**
     * Lee un archivo de propiedades del classpath y registra en el
     * microkernel, por reflexion, cada plugin que declare.
     *
     * @param microkernel      nucleo donde se registraran los plugins.
     * @param classpathResource nombre del recurso, por ejemplo
     *                          {@code "plugins.properties"}.
     * @return cantidad de plugins registrados exitosamente.
     */
    public static int loadFromProperties(QuestionMicrokernel microkernel, String classpathResource) {
        Properties properties = new Properties();
        try (InputStream input = PluginLoader.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (input == null) {
                throw new IllegalStateException("No se encontro el recurso: " + classpathResource);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + classpathResource, e);
        }

        int loaded = 0;
        // TreeMap para un orden de registro determinista (independiente del
        // orden interno del Properties, que no esta garantizado).
        for (Map.Entry<Object, Object> entry : new TreeMap<>(properties).entrySet()) {
            String pluginKey = String.valueOf(entry.getKey());
            String className = String.valueOf(entry.getValue());
            QuestionPlugin plugin = instantiate(className);
            microkernel.registerPlugin(plugin);
            loaded++;
            System.out.println("Plugin registrado por reflexion [" + pluginKey + "] -> " + className);
        }
        return loaded;
    }

    /**
     * Instancia una clase de plugin por su nombre completamente calificado
     * usando reflexion (uso obligatorio segun la guia de la actividad).
     */
    private static QuestionPlugin instantiate(String className) {
        try {
            Class<?> pluginClass = Class.forName(className);
            Constructor<?> constructor = pluginClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            if (!(instance instanceof QuestionPlugin)) {
                throw new IllegalStateException(className + " no implementa QuestionPlugin");
            }
            return (QuestionPlugin) instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo instanciar el plugin: " + className, e);
        }
    }
}
