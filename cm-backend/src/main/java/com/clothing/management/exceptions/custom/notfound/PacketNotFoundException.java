package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class PacketNotFoundException extends EntityNotFoundException {

    public PacketNotFoundException(Long packetId, String packetName) {
        super("Packet", packetId, packetName);
    }
}
