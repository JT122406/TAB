package me.neznamy.tab.shared.features.proxy.message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import me.neznamy.tab.shared.features.proxy.ProxySupport;
import me.neznamy.tab.shared.platform.TabPlayer;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class Load extends ProxyMessage {

    private TabPlayer[] players;
    private PlayerJoin[] decodedPlayers;

    public Load(@NotNull TabPlayer[] players) {
        this.players = players;
    }

    @Override
    public void write(@NotNull ByteArrayDataOutput out) {
        out.writeInt(players.length);
        for (TabPlayer player : players) {
            new PlayerJoin(player).write(out);
        }
    }

    @Override
    public void read(@NotNull ByteArrayDataInput in) {
        int count = in.readInt();
        decodedPlayers = new PlayerJoin[count];
        for (int i=0; i<count; i++) {
            PlayerJoin join = new PlayerJoin();
            join.read(in);
            decodedPlayers[i] = join;
        }
    }

    @Override
    public void process(@NotNull ProxySupport proxySupport) {
        for (PlayerJoin join : decodedPlayers) {
            if (!proxySupport.getProxyPlayers().containsKey(join.getDecodedPlayer().getUniqueId())) {
                join.process(proxySupport);
            }
        }
    }
}
