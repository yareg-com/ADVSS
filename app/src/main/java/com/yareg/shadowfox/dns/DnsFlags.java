package com.yareg.shadowfox.dns;

public class DnsFlags {
    public boolean QR;     // 1 bits
    public int     OpCode; // 4 bits
    public boolean AA;     // 1 bits
    public boolean TC;     // 1 bits
    public boolean RD;     // 1 bits
    public boolean RA;     // 1 bits
    public int     Zero;   // 3 bits
    public int     Rcode;  // 4 bits

    public static DnsFlags Parse(short value) {
        int flags = value & 0xFFFF;

        DnsFlags dnsFlags = new DnsFlags();

        dnsFlags.QR     = ((flags >> 7) & 0x01) == 1;
        dnsFlags.OpCode = (flags >> 3) & 0x0F;
        dnsFlags.AA     = ((flags >> 2) & 0x01) == 1;
        dnsFlags.TC     = ((flags >> 1) & 0x01) == 1;
        dnsFlags.RD     = (flags & 0x01) == 1;
        dnsFlags.RA     = (flags >> 15) == 1;
        dnsFlags.Zero   = (flags >> 12) & 0x07;
        dnsFlags.Rcode  = ((flags >> 8) & 0xF);

        return dnsFlags;
    }

    public short ToShort() {
        short flags = 0;

        flags |= (this.QR ? 1 : 0) << 7;
        flags |= (this.OpCode & 0x0F) << 3;
        flags |= (this.AA ? 1 : 0) << 2;
        flags |= (this.TC ? 1 : 0) << 1;
        flags |= (this.RD ? 1 : 0);
        flags |= (this.RA ? 1 : 0) << 15;
        flags |= (this.Zero & 0x07) << 12;
        flags |= (this.Rcode & 0x0F) << 8;

        return flags;
    }
}
