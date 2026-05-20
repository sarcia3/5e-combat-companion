package org.tcs.model.prototypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;

public class PrototypeWrapper implements Prototype {
    String name;
    transient Prototype realPrototype;

    PrototypeWrapper(){
        name=null;
        realPrototype=null;}

    PrototypeWrapper(String name)
    {
        this.name=name;
        realPrototype=PrototypesLibrary.get(name);
    }

    Prototype getPrototype(){
        return realPrototype;
    }
    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        name=realPrototype.getName();
        oos.defaultWriteObject();
    }
    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();

    }

    @Override
    public String getName() {
        return name;
    }
}
