import React, { useState, useEffect } from 'react';
import { Sofa, Calendar, Shield, Users, CheckCircle, XCircle, Clock, Trash2, ListOrdered, TrendingUp, Info, Lock } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const App = () => {
  const [seats, setSeats] = useState([]);
  const [availableUsers, setAvailableUsers] = useState([]);
  const [blockedDays, setBlockedDays] = useState([]);
  const [selectedDay, setSelectedDay] = useState(new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0]); // Default to tomorrow
  const [myBookings, setMyBookings] = useState([]);
  const [allBookings, setAllBookings] = useState([]);
  const [showModal, setShowModal] = useState(null);
  const [user, setUser] = useState(null);
  const [message, setMessage] = useState(null);
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const today = new Date().toISOString().split('T')[0];
  const tomorrow = new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0];
  const isAfter3PM = true; // DEV MODE: Force true so you can test before 3 PM

  const getCalendarDays = () => {
    const days = [];
    let current = new Date();
    // In strict mode, we only show the next 7 days, but most will be disabled
    for (let i = 0; i < 7; i++) {
        const d = new Date(current);
        d.setDate(current.getDate() + i);
        days.push(d.toISOString().split('T')[0]);
    }
    return days;
  };

  const calendarDays = getCalendarDays();

  useEffect(() => {
    fetchUsers();
    fetchSeats();
    fetchBlockedDays();
  }, []);

  useEffect(() => {
    if (user) {
        fetchMyBookings();
    }
    fetchAllBookingsForDay();
  }, [selectedDay, user?.id]);

  const fetchUsers = async () => {
    try {
      const res = await fetch('/api/admin/users');
      const data = await res.json();
      setAvailableUsers(data);
      if (data.length > 0) {
        const defaultUser = data.find(u => u.role === 'USER') || data[0];
        setUser(defaultUser);
      }
    } catch (e) {}
  };

  const fetchSeats = async () => {
    try {
      const res = await fetch('/api/admin/seats');
      const data = await res.json();
      setSeats(data);
    } catch (e) {}
  };

  const fetchBlockedDays = async () => {
      try {
          const res = await fetch('/api/admin/blocked-days');
          const data = await res.json();
          setBlockedDays(data.map(d => d.date));
      } catch(e){}
  }

  const fetchMyBookings = async () => {
    try {
      const res = await fetch(`/api/bookings/my-bookings?userId=${user.id}`);
      const data = await res.json();
      setMyBookings(data);
    } catch (e) {}
  };

  const fetchAllBookingsForDay = async () => {
    try {
      const res = await fetch(`/api/bookings/daily?date=${selectedDay}`);
      const data = await res.json();
      setAllBookings(data);
    } catch (e) {}
  };

  const handleBooking = async () => {
    try {
      const res = await fetch(`/api/bookings?userId=${user.id}&seatId=${showModal.id}&date=${selectedDay}`, {
        method: 'POST'
      });
      const data = await res.json();
      if (res.ok) {
        setMessage({ type: 'success', text: 'Success! Seat ' + showModal.seatNumber + ' is yours for tomorrow.' });
        fetchMyBookings();
        setShowModal(null);
      } else {
        setMessage({ type: 'error', text: data.message });
      }
    } catch (e) {
      setMessage({ type: 'error', text: 'System offline' });
    }
  };

  const handleWaitlist = async () => {
    try {
      const res = await fetch(`/api/bookings/waitlist?userId=${user.id}&seatId=${showModal.id}&date=${selectedDay}`, {
        method: 'POST'
      });
      if (res.ok) {
        setMessage({ type: 'success', text: 'Added to waitlist. You will be auto-assigned if someone cancels.' });
        setShowModal(null);
      } else {
        const data = await res.json();
        setMessage({ type: 'error', text: data.message });
      }
    } catch (e) {}
  };

  const handleCancel = async (id) => {
    try {
      const res = await fetch(`/api/bookings/cancel/${id}`, { method: 'POST' });
      if (res.ok) {
        setMessage({ type: 'success', text: 'Booking released. Waitlist users notified.' });
        fetchMyBookings();
      }
    } catch (e) {}
  };

  // Logic Helpers
  const isHoliday = (date) => blockedDays.includes(date);
  const isWrongBatch = (date) => {
      if (!user?.squad?.batch?.allowedDays) return false;
      // Correct way to get DAY_OF_WEEK for comparison with Backend Enum
      const dayOfWeek = new Date(date).toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
      return !user.squad.batch.allowedDays.includes(dayOfWeek);
  };
  const isDateDisabled = (date) => {
      if (date !== tomorrow) return true; // ONLY TOMORROW rule
      if (!isAfter3PM) return true;      // 3PM rule
      if (isHoliday(date)) return true;
      if (isWrongBatch(date)) return true;
      return false;
  };

  const isMine = (seatId) => myBookings.some(b => b.seat.id === seatId && b.bookingDate === selectedDay);
  const isBookedByOthers = (seatId) => allBookings.some(b => b.seat.id === seatId && b.user.id !== user?.id);
  
  const occupancyRate = seats.length > 0 ? Math.round((allBookings.length / seats.length) * 100) : 0;

  if (!user) return <div className="app-container" style={{display:'flex', justifyContent:'center', alignItems:'center', height:'100vh', fontSize:'1.5rem'}}>
    <motion.div animate={{rotate: 360}} transition={{repeat: Infinity, duration: 2}}><Clock /></motion.div>
    &nbsp; Enforcing Workspace Rules...
  </div>;

  return (
    <div className="app-container">
      <header>
        <div>
            <div className="logo">SmartSeat <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: '400' }}>Hybrid Desk Manager</span></div>
            <div style={{fontSize:'0.8rem', opacity:0.6, marginTop:4, display:'flex', gap:10}}>
                <span><Clock size={12} /> {currentTime.toLocaleTimeString()}</span>
                <span style={{color: isAfter3PM ? 'var(--accent-secondary)' : 'var(--danger)'}}>
                    {isAfter3PM ? '● Booking Live' : '● Booking Opens at 3 PM'}
                </span>
            </div>
        </div>
        <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
          <div style={{textAlign:'right'}}>
              <div style={{fontSize:'0.8rem', opacity:0.6}}>Working as</div>
              <div style={{fontWeight:'700'}}>{user.name}</div>
          </div>
          <select
            onChange={(e) => {
                const id = parseInt(e.target.value);
                const selected = availableUsers.find(u => u.id === id);
                if (selected) setUser(selected);
                setMessage(null);
            }}
            value={user.id}
            style={{ padding: '0.6rem 1rem', borderRadius: '0.8rem', background: '#1e293b', color: 'white', border: '1px solid var(--glass-border)', outline:'none' }}
          >
            {availableUsers.map(u => (
              <option key={u.id} value={u.id}>Switch User {u.id}</option>
            ))}
          </select>
          <div style={{ padding: '0.5rem 1rem', borderRadius: '2rem', background: 'rgba(59,130,246,0.1)', border:'1px solid rgba(59,130,246,0.2)', fontSize:'0.8rem', color:'var(--accent-primary)', fontWeight:'600'}}>
             {user.squad?.batch?.name || 'ADMIN'}
          </div>
        </div>
      </header>

      {/* Date Enforcer Info Area */}
      <div style={{ background:'rgba(59,130,246,0.05)', padding:'1.5rem', borderRadius:'1rem', border:'1px solid rgba(59,130,246,0.1)', marginBottom:'2rem', display:'flex', justifyContent:'space-between', alignItems:'center'}}>
          <div style={{display:'flex', gap:'1.5rem'}}>
              <div>
                  <div style={{fontSize:'0.7rem', opacity:0.6, textTransform:'uppercase'}}>Your Assigned Days</div>
                  <div style={{fontWeight:'600'}}>{user.squad?.batch?.allowedDays?.join(', ') || 'ALL (Full Access)'}</div>
              </div>
              <div style={{borderLeft:'1px solid rgba(255,255,255,0.1)', paddingLeft:'1.5rem'}}>
                  <div style={{fontSize:'0.7rem', opacity:0.6, textTransform:'uppercase'}}>Booking Status</div>
                  <div style={{fontWeight:'600', color: isAfter3PM ? 'var(--accent-secondary)' : 'var(--text-secondary)'}}>
                      {isAfter3PM ? 'Available for Tomorrow' : 'Opening in ' + (15 - currentTime.getHours() - 1) + 'h ' + (60 - currentTime.getMinutes()) + 'm'}
                  </div>
              </div>
          </div>
          <div style={{textAlign:'right'}}>
              <Info size={16} style={{opacity:0.5}} />
              <div style={{fontSize:'0.75rem', opacity:0.6}}>Rules enforced by Backend</div>
          </div>
      </div>

      <div className="weekly-header">
        {calendarDays.map(day => {
          const disabled = isDateDisabled(day);
          const holiday = isHoliday(day);
          const wrongBatch = isWrongBatch(day);
          
          return (
            <motion.div
              whileHover={!disabled ? { y: -2 } : {}}
              key={day}
              className={'day-tab ' + (selectedDay === day ? 'active' : '')}
              onClick={() => !disabled && setSelectedDay(day)}
              style={{
                  opacity: disabled ? 0.3 : 1,
                  cursor: disabled ? 'not-allowed' : 'pointer',
                  border: holiday ? '1px solid var(--danger)' : (wrongBatch ? '1px solid #f59e0b' : '')
              }}
            >
              <div style={{fontSize:'0.7rem', opacity:0.6, textTransform:'uppercase'}}>
                  {holiday ? 'HOLIDAY' : (wrongBatch ? 'NOT YOUR DAY' : (day !== tomorrow ? 'LOCKED' : (new Date(day).toLocaleDateString(undefined, { weekday: 'short' }))))}
              </div>
              <div style={{fontSize:'1.1rem', fontWeight:'600'}}>{new Date(day).getDate()} {new Date(day).toLocaleDateString(undefined, { month: 'short' })}</div>
            </motion.div>
          );
        })}
      </div>

      <AnimatePresence>
        {message && (
          <motion.div
            initial={{ y: 20, opacity: 0 }} animate={{ y: 0, opacity: 1 }} exit={{ y: 20, opacity: 0 }}
            style={{ padding: '1rem 1.5rem', borderRadius: '1rem', marginBottom: '2rem', background: message.type === 'success' ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)', border: `1px solid ${message.type === 'success' ? '#10b981' : '#ef4444'}`, color: message.type === 'success' ? '#34d399' : '#f87171', display:'flex', justifyContent:'space-between', alignItems:'center'}}
          >
            <div style={{display:'flex', alignItems:'center', gap:'0.75rem'}}>
                {message.type === 'success' ? <CheckCircle size={18} /> : <XCircle size={18} />}
                <span style={{fontWeight:'500'}}>{message.text}</span>
            </div>
            <button onClick={() => setMessage(null)} style={{ background: 'none', color:'inherit', padding:0 }}>Dismiss</button>
          </motion.div>
        )}
      </AnimatePresence>

      <div style={{display:'grid', gridTemplateColumns:'1fr 320px', gap:'2.5rem', alignItems:'start'}}>
        <section>
            <div className="grid-seat-map">
                {seats.map(seat => {
                const mine = isMine(seat.id);
                const bookedByOther = isBookedByOthers(seat.id);
                const isFixedNotMine = seat.type === 'FIXED' && seat.assignedUser && seat.assignedUser.id !== user.id;
                const isLocked = isFixedNotMine || bookedByOther;
                
                return (
                    <motion.div
                        whileHover={!isLocked ? { scale: 1.08 } : {}}
                        key={seat.id}
                        className={`seat ${seat.type.toLowerCase()} ${mine ? 'booked' : ''}`}
                        onClick={() => !isLocked && setShowModal(seat)}
                        style={{
                            background: isLocked ? 'rgba(0,0,0,0.2)' : (mine ? 'rgba(59,130,246,0.2)' : ''),
                            borderColor: isLocked ? 'rgba(255,255,255,0.1)' : (mine ? 'var(--accent-primary)' : ''),
                            opacity: isLocked ? 0.5 : 1,
                            cursor: isLocked ? 'not-allowed' : 'pointer'
                        }}
                    >
                    {isLocked ? <Lock size={18} style={{opacity:0.4}} /> : <Sofa size={22} style={{color: mine ? 'var(--accent-primary)' : 'rgba(255,255,255,0.4)'}} />}
                    <span style={{ marginTop: '6px', fontSize: '0.65rem', fontWeight:'600', opacity:0.8 }}>{seat.seatNumber}</span>
                    {mine && <div style={{position:'absolute', top:-6, right:-6, background: 'var(--accent-primary)', borderRadius:'50%', padding:3}}><CheckCircle size={10} color="white" /></div>}
                    {bookedByOther && !mine && <div style={{position:'absolute', top:-6, right:-6, background: '#f59e0b', borderRadius:'50%', padding:3}}><Users size={10} color="white" /></div>}
                    </motion.div>
                );
                })}
            </div>
        </section>

        <aside style={{display:'flex', flexDirection:'column', gap:'1.5rem'}}>
            <div className="widget" style={{background:'linear-gradient(135deg, rgba(59,130,246,0.1) 0%, rgba(16,185,129,0.1) 100%)'}}>
                <div style={{display:'flex', justifyContent:'space-between', marginBottom:'1rem'}}>
                    <h3 style={{fontSize:'0.9rem', opacity:0.6, textTransform:'uppercase', letterSpacing:1}}>Seat Occupancy</h3>
                    <TrendingUp size={16} color="var(--accent-secondary)" />
                </div>
                <div style={{fontSize:'2.5rem', fontWeight:'700'}}>{occupancyRate}%</div>
                <div style={{width:'100%', height:6, background:'rgba(255,255,255,0.1)', borderRadius:10, marginTop:'0.75rem', overflow:'hidden'}}>
                    <motion.div initial={{width:0}} animate={{width: `${occupancyRate}%`}} style={{height:'100%', background:'linear-gradient(90deg, #3b82f6, #10b981)'}} />
                </div>
                <p style={{fontSize:'0.75rem', color:'var(--text-secondary)', marginTop:'1rem'}}>Occupancy for selected day</p>
            </div>

            <div className="widget">
                <h3 style={{fontSize:'0.9rem', opacity:0.6, textTransform:'uppercase', letterSpacing:1, marginBottom:'1.5rem'}}>Upcoming</h3>
                <div style={{display:'flex', flexDirection:'column', gap:'1.25rem'}}>
                    {myBookings.length === 0 && <p style={{color:'var(--text-secondary)', fontSize:'0.85rem', textAlign:'center', padding:'1rem'}}>No reservations.</p>}
                    {myBookings.map(b => (
                        <div key={b.id} style={{display:'flex', justifyContent:'space-between', alignItems:'center', background:'rgba(255,255,255,0.03)', padding:'0.75rem 1rem', borderRadius:'0.75rem', border:'1px solid var(--glass-border)'}}>
                            <div style={{display:'flex', gap:'1rem', alignItems:'center'}}>
                                <div style={{width:8, height:8, borderRadius:'50%', background: b.bookingDate === tomorrow ? 'var(--accent-secondary)' : 'var(--text-secondary)'}}></div>
                                <div>
                                    <div style={{fontWeight:'700', fontSize:'0.9rem'}}>Desk {b.seat.seatNumber}</div>
                                    <div style={{fontSize:'0.75rem', color:'var(--text-secondary)'}}>{new Date(b.bookingDate).toLocaleDateString(undefined, {month:'short', day:'numeric', weekday:'short'})}</div>
                                </div>
                            </div>
                            <button onClick={() => handleCancel(b.id)} style={{background:'none', color:'#ef4444', padding:0}}> <Trash2 size={16} /> </button>
                        </div>
                    ))}
                </div>
            </div>
        </aside>
      </div>

      <AnimatePresence>
        {showModal && (
            <motion.div initial={{opacity:0}} animate={{opacity:1}} exit={{opacity:0}} className="modal-overlay" onClick={() => setShowModal(null)}>
                <motion.div initial={{scale:0.9, y: 20}} animate={{scale:1, y: 0}} exit={{scale:0.9, y: 20}} className="modal" onClick={e => e.stopPropagation()}>
                    <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:'1.5rem'}}>
                        <h3 style={{fontSize:'1.5rem'}}>Reserve {showModal.seatNumber}</h3>
                        <div style={{padding:'4px 12px', borderRadius:20, fontSize:'0.7rem', fontWeight:'700', background:'rgba(59,130,246,0.1)', color:'var(--accent-primary)', border:'1px solid var(--glass-border)'}}> {showModal.type} </div>
                    </div>
                    
                    <div style={{ background:'rgba(255,255,255,0.03)', padding:'1.5rem', borderRadius:'1rem', border:'1px solid var(--glass-border)', marginBottom:'2rem'}}>
                        <div style={{ color: 'var(--text-secondary)', fontSize:'0.9rem', display:'flex', alignItems:'center', gap:'0.75rem', marginBottom:'0.75rem' }}>
                            <Calendar size={16} color="var(--accent-primary)" /> Tomorrow: {new Date(tomorrow).toDateString()}
                        </div>
                        <div style={{ color: 'var(--text-secondary)', fontSize:'0.9rem', display:'flex', alignItems:'center', gap:'0.75rem' }}>
                            <Clock size={16} color="var(--accent-secondary)" /> Windows: opens 3:00 PM Daily
                        </div>
                    </div>

                    <div style={{ display: 'flex', flexDirection:'column', gap: '0.75rem' }}>
                        <button onClick={handleBooking} disabled={isMine(showModal.id)} style={{ width:'100%', height:'3.5rem', justifyContent:'center'}}>
                            {isMine(showModal.id) ? 'ALREADY BOOKED' : 'CONFIRM FOR TOMORROW'}
                        </button>
                        <button onClick={handleWaitlist} style={{ width:'100%', height:'3rem', justifyContent:'center', background: 'none', border:'1px solid var(--glass-border)' }}>
                            JOIN WAITING LIST
                        </button>
                    </div>
                </motion.div>
            </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default App;
