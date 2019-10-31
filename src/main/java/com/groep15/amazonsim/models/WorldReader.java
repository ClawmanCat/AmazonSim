package com.groep15.amazonsim.models;

import com.groep15.amazonsim.models.worldobject.Object3D;
import com.groep15.amazonsim.models.worldobject.Object3DFactory;
import org.javatuples.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Construct a world object from a world definition file.
public final class WorldReader {
    private WorldReader() {}

    public static World ReadWorld(String path) {
        // TODO: Merge everything into one pass.

        try {
            List<String> contents = Files.readAllLines(Paths.get(path), StandardCharsets.US_ASCII);

            Map<Character, JSONObject[]> legend = new HashMap<>();

            // 1st pass: read legend.
            boolean inLegend = false;
            for (String line : contents) {
                // Set state to start reading legend.
                if (line.equals("#BEGINLEGEND")) { inLegend = true; continue; }
                // Set state to stop reading legend.
                if (line.equals("#ENDLEGEND")) { inLegend = false; continue; }

                // Read legend.
                if (inLegend) {
                    Pair<Character, JSONObject[]> kv = DecodeLegendEntry(line);
                    legend.put(kv.getValue0(), kv.getValue1());
                }
            }

            // 2nd pass: read world layout.
            World result = new World(1, 1);
            boolean inLayout = false;
            int zCoord = 0;
            int xMax = 0;

            for (String line : contents) {
                // Set state to start reading layout.
                if (line.equals("#BEGINLAYOUT")) { inLayout = true; continue; }
                // Set state to stop reading layout.
                if (line.equals("#ENDLAYOUT")) { inLayout = false; continue; }

                // Read layout contents.
                if (inLayout) {
                    for (int xCoord = 0; xCoord < line.length(); ++xCoord) {
                        for (JSONObject jso : legend.get(line.charAt(xCoord))) {
                            Object3D o = Object3DFactory.Create(jso, result);
                            o.setPosition(xCoord, o.getPosition().y, zCoord);

                            result.addWorldObject(o);
                        }

                        if (xCoord > xMax) xMax = xCoord;
                    }

                    ++zCoord;
                }
            }

            result.setSize(xMax + 1, zCoord);

            // 3rd pass: load properties
            boolean inProperties = false;
            StringBuilder propstring = new StringBuilder();

            for (String line : contents) {
                // Set state to start reading properties.
                if (line.equals("#BEGINPROPERTIES")) { inProperties = true; continue; }
                // Set state to stop reading properties.
                if (line.equals("#ENDPROPERTIES")) { inProperties = false; continue; }

                // Read properties
                if (inProperties) propstring.append(line).append(" ");
            }

            JSONParser parser = new JSONParser();
            result.loadSettings((JSONObject) parser.parse(propstring.toString()));

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Reading world from disk failed: " + e.getMessage());
        }
    }


    private static Pair<Character, JSONObject[]> DecodeLegendEntry(String entry) {
        JSONObject lineJSON = (JSONObject) JSONValue.parse(entry);

        Character key = (Character) ((String) lineJSON.get("key")).charAt(0);
        JSONArray val = (JSONArray) lineJSON.get("value");

        JSONObject[] objects = new JSONObject[val.size()];
        for (int i = 0; i < val.size(); ++i) objects[i] = (JSONObject) val.get(i);

        return new Pair<>(key, objects);
    }
}
