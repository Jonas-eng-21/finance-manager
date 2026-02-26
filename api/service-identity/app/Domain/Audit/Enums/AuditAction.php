<?php

namespace App\Domain\Audit\Enums;

enum AuditAction: string
{
    case LOGIN_SUCCESS = 'LOGIN_SUCCESS';
    case LOGIN_FAILED = 'LOGIN_FAILED';
    case LOGOUT = 'LOGOUT';
    case PASSWORD_CHANGED = 'PASSWORD_CHANGED';
    case PROFILE_UPDATED = 'PROFILE_UPDATED';
    case USER_REGISTERED = 'USER_REGISTERED';
    case USER_DELETED = 'USER_DELETED';
}
