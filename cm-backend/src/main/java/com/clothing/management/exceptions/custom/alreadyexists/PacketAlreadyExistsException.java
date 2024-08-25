package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class PacketAlreadyExistsException extends AlreadyExistsException {

    public PacketAlreadyExistsException(Long packetId, String packetName) {
        super("Packet", packetId, packetName);
    }
}
