/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data.basic;

import java.io.IOException;

/**
 * A generic reader interface for additional consistency.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <In> output of the reader (type to be read)
 * @param <InData> additional data for the reader (if it requires any)
 */
public interface Reader<In, InData> {

    /**
     * Opens the reader for reading.
     *
     * @throws IOException exception when opening
     */
    void open() throws IOException;

    /**
     * Reads the input and returns the output
     *
     * @param in additional data
     * @return read output
     * @throws IOException exception when reading
     */
    In read( InData in ) throws IOException;

    /**
     * Closes the reader.
     *
     * @throws IOException exception when closing
     */
    void close() throws IOException;

    /**
     * Returns true if the reader is open, false otherwise
     *
     * @return true or false
     */
    boolean isOpen();
}
