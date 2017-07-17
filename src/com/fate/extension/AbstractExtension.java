package com.fate.extension;

import com.fate.world.Player;

import java.nio.ByteBuffer;

public interface AbstractExtension {

    void init();

    void dispacth(Player player, ByteBuffer byteBuffer, int channel, int roomId);
}
