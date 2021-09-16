/*
 * Copyright 2021, Brightspot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.gke;

import com.google.container.v1.ReleaseChannel;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeReleaseChannel extends Diffable implements Copyable<ReleaseChannel> {

    private ReleaseChannel.Channel channel;

    /**
     * The release channel the cluster is subscribed to.
     */
    @Required
    @Updatable
    @ValidStrings({ "RAPID", "REGULAR", "STABLE" })
    public ReleaseChannel.Channel getChannel() {
        return channel;
    }

    public void setChannel(ReleaseChannel.Channel channel) {
        this.channel = channel;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ReleaseChannel model) throws Exception {
        setChannel(model.getChannel());
    }

    ReleaseChannel toReleaseChannel() {
        return ReleaseChannel.newBuilder().setChannel(getChannel()).build();
    }
}
