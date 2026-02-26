<?php

namespace App\Infrastructure\Persistence\Eloquent;

use App\Domain\Audit\AuditLog;
use App\Domain\Audit\AuditLogRepositoryInterface;

class EloquentAuditLogRepository implements AuditLogRepositoryInterface
{
    public function save(AuditLog $auditLog): void
    {
        AuditLogModel::create([
            'id' => $auditLog->getId(),
            'action' => $auditLog->getAction()->value,
            'user_id' => $auditLog->getUserId(),
            'ip_address' => $auditLog->getIpAddress(),
            'user_agent' => $auditLog->getUserAgent(),
            'created_at' => $auditLog->getCreatedAt()->format('Y-m-d H:i:s'),
        ]);
    }
}
