package kr.saebyeok.santafy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoSanta {

    private static final File FILE = new File("plugins/Santafy/nosanta.dat");

    private final Set<UUID> set;

    @SuppressWarnings("unchecked")
    public NoSanta() {
        if (!FILE.exists()) {
            this.set = new HashSet<>();
        } else {
            Set<UUID> set;
            try (final ObjectInputStream stream = new ObjectInputStream(new FileInputStream(FILE))) {
                set = (Set<UUID>) stream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                set = new HashSet<>();
            }
            this.set = set;
        }
    }

    public boolean getState(UUID uuid) {
        return !set.contains(uuid);
    }

    public boolean toggleState(UUID uuid) {
        if (set.remove(uuid)) {
            return true;
        } else {
            set.add(uuid);
            return false;
        }
    }

    public boolean save() {
        FILE.getParentFile().mkdirs();
        try (final ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(FILE))) {
            FILE.createNewFile();
            stream.writeObject(set);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
