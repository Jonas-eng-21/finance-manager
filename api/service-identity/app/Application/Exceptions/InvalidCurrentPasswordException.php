<?php

namespace App\Application\Exceptions;

use Exception;

class InvalidCurrentPasswordException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.invalid_current_password')
    {
        parent::__construct($message);
    }
}
