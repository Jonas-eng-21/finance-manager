<?php

namespace App\Infrastructure\Events;

use App\Application\Contracts\EventDispatcherInterface;
use Illuminate\Support\Facades\Event;

class LaravelEventDispatcher implements EventDispatcherInterface
{
    public function dispatch(object $event): void
    {
        Event::dispatch($event);
    }
}
