<?php

namespace App\Application\Exceptions;

use Exception;

class InvalidCredentialsException extends Exception
{
    public function __construct(string $message = 'identity.auth.errors.invalid_credentials')
    {
        parent::__construct($message);
    }
}
