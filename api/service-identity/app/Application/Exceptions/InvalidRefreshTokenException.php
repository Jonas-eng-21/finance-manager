<?php

namespace App\Application\Exceptions;

use Exception;

class InvalidRefreshTokenException extends Exception
{
    public function __construct()
    {
        parent::__construct('identity.auth.errors.invalid_refresh_token');
    }
}
