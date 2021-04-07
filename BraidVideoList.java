import java.net.MalformedURLException;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

public class BraidVideoList {

    // Aggregate object class is a wrapper around three
    // entities in our cache.
    class Aggregate {
        String name;
        URL url;
        BufferedImage image;

        public Aggregate(String n, URL u, BufferedImage i) {
            name = n;
            url = u;
            image = i;
        }
    }

    // Declared type is Map (interface), so that we can
    // easily change implementation later if needed
    private Map<BufferedImage, Aggregate> picMap;
    private Map<String, Aggregate> nameMap;
    private Map<URL, Aggregate> urlMap;

    // WeakHashMap uses WeakReference as keys in the map. This works well for
    // our scenario since we want to remove entries when the Aggregate object
    // no longer holds strong references to the objects.
    //
    // For instance, if a name is updated,
    // then the corresponding mapping from name to Aggregate in nameMap
    // should be removed. This can be done automatically by replacing the name
    // reference in the Aggregate object. Since no other strong references to
    // this object exist, we can rely on garbage collector to safely remove
    // the outdated mapping.
    public BraidVideoList() {
        picMap = new WeakHashMap<BufferedImage, Aggregate>();
        nameMap = new WeakHashMap<String, Aggregate>();
        urlMap = new WeakHashMap<URL, Aggregate>();
    }

    // Insert a new entry into our cache.
    public boolean insert(String name, URL url, BufferedImage pic) {
        if (name == null || url == null || pic == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (picMap.containsKey(pic) || urlMap.containsKey(url) || nameMap.containsKey(name)) {
            return false;

        }

        Aggregate combination = new Aggregate(name, url, pic);

        picMap.put(pic, combination);
        urlMap.put(url, combination);
        nameMap.put(name, combination);

        return true;
    }

    // Given a name, update the url associated with the name.
    // Returns true if succeeded, else returns false.
    // Throws exception if null arguments are passed in.
    public boolean update(String name, URL url) {
        if (name == null || url == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (urlMap.containsKey(url) || !nameMap.containsKey(name)) {
            return false;
        }

        Aggregate combination = nameMap.get(name);

        combination.url = url;
        urlMap.put(url, combination);

        return true;
    }

    // Given an oldName, update the name to be newName.
    // Returns true if succeeded, else returns false.
    // Throws exception if null arguments are passed in.
    public boolean update(String oldName, String newName) {
        if (oldName == null || newName == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (nameMap.containsKey(newName) || !nameMap.containsKey(oldName)) {
            return false;
        }

        Aggregate combination = nameMap.get(oldName);

        combination.name = newName;
        nameMap.put(newName, combination);

        return true;
    }

    // Given a name, update the pic associated with the name.
    // Returns true if succeeded, else returns false.
    // Throws exception if null arguments are passed in.
    public boolean update(String name, BufferedImage pic) {
        if (name == null || pic == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (picMap.containsKey(pic) || !nameMap.containsKey(name)) {
            return false;
        }

        Aggregate combination = nameMap.get(name);

        combination.image = pic;
        picMap.put(pic, combination);

        return true;
    }

    // Given a pic, update the name associated with the pic.
    // Returns true if succeeded, else returns false.
    // Throws exception if null arguments are passed in.
    public boolean update(BufferedImage pic, String name) {
        if (pic == null || name == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (nameMap.containsKey(name) || !picMap.containsKey(pic)) {
            return false;
        }

        Aggregate combination = picMap.get(pic);

        combination.name = name;
        nameMap.put(name, combination);

        return true;
    }

    // Given a name, return the Aggregate object that holds
    // name, image, and url.
    public Aggregate query(String name) {
        return nameMap.get(name);
    }

    // Given a pic, return the Aggregate object that holds
    // name, image, and url.
    public Aggregate query(BufferedImage pic) {
        return picMap.get(pic);
    }

    public static void main(String[] args) {
        try {
            URL url = new URL("http://www.google.com");
            BufferedImage pic = ImageIO.read(new File("google.png"));
            String name = "google";

            BraidVideoList vidList = new BraidVideoList();
            vidList.insert(name, url, pic);

            assert (vidList.query(name) == vidList.query(pic));
            assert (vidList.query(name).name.equals(name));
            assert (vidList.query(name).url.toString().equals("http://www.google.com"));

            // now we test updating the name of the picture
            vidList.update(name, "new google");

            assert (vidList.query(name) == null);
            assert (vidList.query("new google") == vidList.query(pic));
            assert (vidList.query(pic).name.equals("new google"));
            assert (vidList.query("new google").url.toString().equals("http://www.google.com"));

            // just to test our WeakReference has been removed from the map
            assert (vidList.nameMap.size() == 1);
            assert (vidList.picMap.size() == 1);
            assert (vidList.urlMap.size() == 1);
        } catch (MalformedURLException e) {
            System.err.println("MalformedURL");
            return;
        } catch (IOException e) {
            System.err.println("IOException caught");
            return;
        }

        System.out.println("Exiting simple test driver with success");
    }
}