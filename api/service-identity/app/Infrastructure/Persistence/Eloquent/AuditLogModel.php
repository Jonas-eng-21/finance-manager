<?php

namespace App\Infrastructure\Persistence\Eloquent;

use Illuminate\Database\Eloquent\Model;

class AuditLogModel extends Model
{
    protected $table = 'audit_logs';

    public $incrementing = false;
    protected $keyType = 'string';

    public const UPDATED_AT = null;

    protected $fillable = [
        'id',
        'action',
        'user_id',
        'ip_address',
        'user_agent',
        'created_at',
    ];
}
