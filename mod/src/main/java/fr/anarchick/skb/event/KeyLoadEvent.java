package fr.anarchick.skb.event;

import com.mojang.datafixers.util.Function7;
import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.PluginChannels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record KeyLoadEvent(byte size, byte i, Identifier identifier, String name, String description, String category, short keyCode) implements CustomPayload {

    public static final CustomPayload.Id<KeyLoadEvent> CHANNEL = new CustomPayload.Id<>(PluginChannels.LOAD_KEYS.getId());
    public static final PacketCodec<RegistryByteBuf, KeyLoadEvent> CODEC = tuple(
            PacketCodecs.BYTE, KeyLoadEvent::size,
            PacketCodecs.BYTE, KeyLoadEvent::i,
            Identifier.PACKET_CODEC, KeyLoadEvent::identifier,
            PacketCodecs.STRING, KeyLoadEvent::name,
            PacketCodecs.STRING, KeyLoadEvent::description,
            PacketCodecs.STRING, KeyLoadEvent::category,
            PacketCodecs.SHORT, KeyLoadEvent::keyCode,
            KeyLoadEvent::new
    );

    // Vanilla is limited to T6
    private static <B, C, T1, T2, T3, T4, T5, T6, T7> PacketCodec<B, C> tuple(
            final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1,
            final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2,
            final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3,
            final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4,
            final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5,
            final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6,
            final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> to) {
        return new PacketCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = codec1.decode(object);
                T2 object3 = codec2.decode(object);
                T3 object4 = codec3.decode(object);
                T4 object5 = codec4.decode(object);
                T5 object6 = codec5.decode(object);
                T6 object7 = codec6.decode(object);
                T7 object8 = codec7.decode(object);
                return to.apply(object2, object3, object4, object5, object6, object7, object8);
            }

            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
            }
        };
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return CHANNEL;
    }

    public void load() {
        MinecraftClient client = MinecraftClient.getInstance();
        KeyEntry keyEntry = new KeyEntry(identifier, name, description, category, keyCode);

        // Read data async and then use client.execute() after for thread safety.
        client.execute(() -> {

            if (i == 1) { // only the first packet will reset the key entries
                ServerKeyboardBridge.clearKeyEntries();
                client.getToastManager().add(
                        SystemToast.create(client, SystemToast.Type.NARRATOR_TOGGLE,
                                Text.translatable("serverKeyboardBridge.toast.title"),
                                Text.translatable("serverKeyboardBridge.toast.description")
                        )
                );
            }

            ServerKeyboardBridge.addKeyEntry(keyEntry);

            if (i == size) { // last packet
                ServerKeyboardBridge.reload();
            }

        });
    }

}
