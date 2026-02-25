<?php

namespace App\Domain\User\Exceptions;

use Exception;

class SamePasswordException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.same_password')
    {
        parent::__construct($message);
    }
}
