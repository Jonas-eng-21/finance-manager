<?php

namespace Tests\Unit\Domain\Audit;

use App\Domain\Audit\AuditLog;
use App\Domain\Audit\Enums\AuditAction;
use DateTimeImmutable;
use PHPUnit\Framework\TestCase;

class AuditLogTest extends TestCase
{
    public function test_it_creates_an_audit_log_entry(): void
    {
        $log = new AuditLog(
            id: 'uuid-1234',
            action: AuditAction::LOGIN_FAILED,
            userId: 99,
            ipAddress: '192.168.1.100',
            userAgent: 'Mozilla/5.0 (Windows NT 10.0)',
            createdAt: new DateTimeImmutable('2026-02-26 14:00:00')
        );

        $this->assertEquals('uuid-1234', $log->getId());
        $this->assertEquals(AuditAction::LOGIN_FAILED, $log->getAction());
        $this->assertEquals(99, $log->getUserId());
        $this->assertEquals('192.168.1.100', $log->getIpAddress());
        $this->assertEquals('Mozilla/5.0 (Windows NT 10.0)', $log->getUserAgent());
    }
}
