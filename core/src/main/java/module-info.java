/*
 * This file is part of Krypton Serialization, licensed under the MIT license.
 *
 * Copyright (C) 2022 KryptonMC and contributors
 *
 * This project is licensed under the terms of the MIT license.
 * For more details, please reference the LICENSE file in the top-level directory.
 */
module org.kryptonmc.serialization {
    exports org.kryptonmc.serialization;
    exports org.kryptonmc.serialization.codecs;
    exports org.kryptonmc.util;
    exports org.kryptonmc.util.function;
    exports org.kryptonmc.util.functional;

    requires static transitive org.jetbrains.annotations;
}