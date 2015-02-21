/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache.distributed;

import org.apache.ignite.internal.processors.cache.version.*;
import org.apache.ignite.internal.util.tostring.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.plugin.extensions.communication.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.*;

/**
 * Transaction completion message.
 */
public class GridDistributedTxFinishRequest<K, V> extends GridDistributedBaseMessage<K, V> {
    /** */
    private static final long serialVersionUID = 0L;

    /** Future ID. */
    private IgniteUuid futId;

    /** Thread ID. */
    private long threadId;

    /** Commit version. */
    private GridCacheVersion commitVer;

    /** Invalidate flag. */
    private boolean invalidate;

    /** Commit flag. */
    private boolean commit;

    /** Sync commit flag. */
    private boolean syncCommit;

    /** Sync commit flag. */
    private boolean syncRollback;

    /** Expected txSize. */
    private int txSize;

    /** System flag. */
    private boolean sys;

    /**
     * Empty constructor required by {@link Externalizable}.
     */
    public GridDistributedTxFinishRequest() {
        /* No-op. */
    }

    /**
     * @param xidVer Transaction ID.
     * @param futId future ID.
     * @param threadId Thread ID.
     * @param commitVer Commit version.
     * @param commit Commit flag.
     * @param invalidate Invalidate flag.
     * @param sys System flag.
     * @param txSize Expected transaction size.
     */
    public GridDistributedTxFinishRequest(
        GridCacheVersion xidVer,
        IgniteUuid futId,
        @Nullable GridCacheVersion commitVer,
        long threadId,
        boolean commit,
        boolean invalidate,
        boolean sys,
        boolean syncCommit,
        boolean syncRollback,
        int txSize
    ) {
        super(xidVer, 0);
        assert xidVer != null;

        this.futId = futId;
        this.commitVer = commitVer;
        this.threadId = threadId;
        this.commit = commit;
        this.invalidate = invalidate;
        this.sys = sys;
        this.syncCommit = syncCommit;
        this.syncRollback = syncRollback;
        this.txSize = txSize;
    }

    /**
     * @return System flag.
     */
    public boolean system() {
        return sys;
    }

    /**
     * @return Future ID.
     */
    public IgniteUuid futureId() {
        return futId;
    }

    /**
     * @return Thread ID.
     */
    public long threadId() {
        return threadId;
    }

    /**
     * @return Commit version.
     */
    public GridCacheVersion commitVersion() {
        return commitVer;
    }

    /**
     * @return Commit flag.
     */
    public boolean commit() {
        return commit;
    }

    /**
     *
     * @return Invalidate flag.
     */
    public boolean isInvalidate() {
        return invalidate;
    }

    /**
     * @return Sync commit flag.
     */
    public boolean syncCommit() {
        return syncCommit;
    }

    /**
     * @return Sync rollback flag.
     */
    public boolean syncRollback() {
        return syncRollback;
    }

    /**
     * @return Expected tx size.
     */
    public int txSize() {
        return txSize;
    }

    /**
     *
     * @return {@code True} if reply is required.
     */
    public boolean replyRequired() {
        return commit ? syncCommit : syncRollback;
    }

    /** {@inheritDoc} */
    @Override public boolean writeTo(ByteBuffer buf, MessageWriter writer) {
        writer.setBuffer(buf);

        if (!super.writeTo(buf, writer))
            return false;

        if (!writer.isTypeWritten()) {
            if (!writer.writeByte(null, directType()))
                return false;

            writer.onTypeWritten();
        }

        switch (writer.state()) {
            case 6:
                if (!writer.writeBoolean("commit", commit))
                    return false;

                writer.incrementState();

            case 7:
                if (!writer.writeMessage("commitVer", commitVer))
                    return false;

                writer.incrementState();

            case 8:
                if (!writer.writeIgniteUuid("futId", futId))
                    return false;

                writer.incrementState();

            case 9:
                if (!writer.writeBoolean("invalidate", invalidate))
                    return false;

                writer.incrementState();

            case 10:
                if (!writer.writeBoolean("syncCommit", syncCommit))
                    return false;

                writer.incrementState();

            case 11:
                if (!writer.writeBoolean("syncRollback", syncRollback))
                    return false;

                writer.incrementState();

            case 12:
                if (!writer.writeBoolean("sys", sys))
                    return false;

                writer.incrementState();

            case 13:
                if (!writer.writeLong("threadId", threadId))
                    return false;

                writer.incrementState();

            case 14:
                if (!writer.writeInt("txSize", txSize))
                    return false;

                writer.incrementState();

        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public boolean readFrom(ByteBuffer buf) {
        reader.setBuffer(buf);

        if (!super.readFrom(buf))
            return false;

        switch (readState) {
            case 6:
                commit = reader.readBoolean("commit");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 7:
                commitVer = reader.readMessage("commitVer");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 8:
                futId = reader.readIgniteUuid("futId");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 9:
                invalidate = reader.readBoolean("invalidate");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 10:
                syncCommit = reader.readBoolean("syncCommit");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 11:
                syncRollback = reader.readBoolean("syncRollback");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 12:
                sys = reader.readBoolean("sys");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 13:
                threadId = reader.readLong("threadId");

                if (!reader.isLastRead())
                    return false;

                readState++;

            case 14:
                txSize = reader.readInt("txSize");

                if (!reader.isLastRead())
                    return false;

                readState++;

        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public byte directType() {
        return 23;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return GridToStringBuilder.toString(GridDistributedTxFinishRequest.class, this,
            "super", super.toString());
    }
}
