package org.kryptonmc.serialization.nbt;

import java.util.stream.Stream;
import org.kryptonmc.nbt.ByteTag;
import org.kryptonmc.nbt.IntTag;
import org.kryptonmc.nbt.LongTag;
import org.kryptonmc.nbt.Tag;

final class ArrayUtil {

    static byte[] toByteArray(final Stream<Tag> stream) {
        final var list = stream.toList();
        final var result = new byte[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((ByteTag) list.get(i)).value();
        }
        return result;
    }

    static int[] toIntArray(final Stream<Tag> stream) {
        final var list = stream.toList();
        final var result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((IntTag) list.get(i)).value();
        }
        return result;
    }

    static long[] toLongArray(final Stream<Tag> stream) {
        final var list = stream.toList();
        final var result = new long[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((LongTag) list.get(i)).value();
        }
        return result;
    }

    private ArrayUtil() {
    }
}
