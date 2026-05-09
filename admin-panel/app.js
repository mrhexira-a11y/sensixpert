// Firebase config
firebase.initializeApp({
  apiKey:"AIzaSyC5osaob2UbMEn1vcd9PaWCHBiCs-6L7sk",
  authDomain:"sensixpert-4b68f.firebaseapp.com",
  projectId:"sensixpert-4b68f",
  storageBucket:"sensixpert-4b68f.firebasestorage.app",
  messagingSenderId:"21963652669",
  appId:"1:21963652669:android:4316962c2886296b533454"
});
const auth=firebase.auth(), db=firebase.firestore();
const ADMIN_EMAILS=["sensixpertadmin@gmail.com","admin@sensixpert.com","mrhexira@gmail.com"];
const BACKEND="https://sensixpert-backend.onrender.com";
let revenueChart,usersChart,revenueChart2,planChart2;
let loginAttempts=0,lockoutUntil=0;

// OneSignal config
const ONESIGNAL_APP_ID='e83708b1-ef26-4755-9309-d5aeb64c734e';
const ONESIGNAL_API_KEY='os_v2_app_2vkd2hsksueofedhd76h2zxdc7hycwlkh7lqk5oxjmzprhvotjcylhpkrjfyqwqowv655lngd3wz74a4nqlwdtjw4a4yqdbcx4yq5q';

// ─── AUTH (SECURE) ───
async function login(){
  const email=document.getElementById('loginEmail').value;
  const pass=document.getElementById('loginPassword').value;
  const err=document.getElementById('loginError');
  err.textContent='';
  // Brute force protection
  if(Date.now()<lockoutUntil){err.textContent='Too many attempts. Try again in '+ Math.ceil((lockoutUntil-Date.now())/1000)+'s';return}
  if(!email||!pass){err.textContent='Enter email and password';return}
  try{
    const r=await auth.signInWithEmailAndPassword(email,pass);
    if(!ADMIN_EMAILS.includes(r.user.email)){err.textContent='❌ Not authorized as admin';auth.signOut();return;}
    loginAttempts=0;
    showDashboard();
  }catch(e){
    loginAttempts++;
    if(loginAttempts>=5){lockoutUntil=Date.now()+60000;err.textContent='🔒 Locked for 60 seconds (too many attempts)'}
    else{err.textContent='❌ '+e.message.replace('Firebase:','')+` (${5-loginAttempts} attempts left)`}
  }
}
function logout(){auth.signOut();location.reload();}
auth.onAuthStateChanged(u=>{if(u&&ADMIN_EMAILS.includes(u.email))showDashboard()});

function showDashboard(){
  document.getElementById('loginScreen').style.display='none';
  document.getElementById('dashboardScreen').style.display='block';
  refreshAll();
}

// ─── NAVIGATION ───
function showSection(name,el){
  document.querySelectorAll('.section').forEach(s=>s.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n=>n.classList.remove('active'));
  document.getElementById('sec-'+name).classList.add('active');
  if(el)el.classList.add('active');
  const titles={dashboard:'Dashboard',users:'Users',payments:'Payments',subscriptions:'Subscriptions',analytics:'Analytics',notifications:'Notifications',pricing:'Pricing',policies:'Policies'};
  document.getElementById('pageTitle').textContent=titles[name]||name;
  if(name==='notifications')loadNotifications();
  if(name==='pricing')loadPricing();
  if(name==='policies')loadPolicies();
  if(name==='analytics')renderAnalytics();
}

// ─── REFRESH ALL (sequential to avoid race condition) ───
let allUsers=[],allPayments=[];
async function refreshAll(){
  const btn=document.querySelector('.btn-refresh');
  if(btn){btn.classList.add('spinning');setTimeout(()=>btn.classList.remove('spinning'),600)}
  try{
    // Load data sequentially to avoid race conditions on shared arrays
    const uSnap=await db.collection('users').get();
    const pSnap=await db.collection('payments').orderBy('createdAt','desc').get();
    allUsers=[];allPayments=[];
    uSnap.forEach(d=>{allUsers.push({id:d.id,...d.data()})});
    pSnap.forEach(d=>{allPayments.push({id:d.id,...d.data()})});

    // Now render everything from the loaded data
    renderDashboard();
    renderUsers();
    renderPayments();
    checkServer();
  }catch(e){console.error('Refresh error:',e)}
}

// ─── SERVER HEALTH ───
async function checkServer(){
  const dot=document.getElementById('serverDot');
  const txt=document.getElementById('serverText');
  try{
    const r=await fetch(BACKEND,{signal:AbortSignal.timeout(10000)});
    const d=await r.json();
    if(dot)dot.className='status-dot';
    if(txt)txt.textContent='Server Online';
  }catch(e){
    if(dot)dot.className='status-dot offline';
    if(txt)txt.textContent='Server Offline';
  }
}

// ─── DASHBOARD ───
function renderDashboard(){
  try{
    let active=0,totalRev=0,pending=0,todayRev=0;
    const today=new Date().toDateString();
    allUsers.forEach(u=>{const s=u.subscription||{};if(s.status==='active'&&s.endDate>Date.now())active++});
    allPayments.forEach(p=>{
      if(p.status==='Success'||p.status==='success'){totalRev+=p.amount||0;
        if(p.createdAt&&new Date(p.createdAt.seconds*1000).toDateString()===today)todayRev+=p.amount||0}
      if(p.status==='pending')pending++;
    });

    document.getElementById('statUsers').textContent=allUsers.length;
    document.getElementById('statActive').textContent=active;
    document.getElementById('statRevenue').textContent='₹'+totalRev.toLocaleString('en-IN');
    document.getElementById('statPending').textContent=pending;
    document.getElementById('statToday').textContent='₹'+todayRev.toLocaleString('en-IN');
    document.getElementById('statConversion').textContent=allUsers.length?Math.round(active/allUsers.length*100)+'%':'0%';

    // Recent payments table
    let html='';
    allPayments.slice(0,10).forEach(p=>{
      const date=p.createdAt?new Date(p.createdAt.seconds*1000).toLocaleString('en-IN',{dateStyle:'short',timeStyle:'short'}):'—';
      const sc=p.status==='pending'?'pending':(p.status==='Success'||p.status==='success')?'success':'failed';
      html+=`<tr><td>${p.id.substring(0,20)}</td><td>${(p.userId||'').substring(0,12)}..</td><td>${p.plan||'—'}</td><td>₹${p.amount||0}</td><td><span class="badge ${sc}">${p.status||'—'}</span></td><td>${date}</td></tr>`;
    });
    document.getElementById('recentPayments').innerHTML=html||'<tr><td colspan="6" class="empty">No payments yet</td></tr>';
    renderCharts('revenueChart','planChart');
  }catch(e){console.error('Dashboard error:',e)}
}

// ─── CHARTS (reusable for both dashboard and analytics) ───
function renderCharts(revenueCanvasId, planCanvasId){
  // Revenue by day (last 7 days)
  const days=[],revData=[];
  for(let i=6;i>=0;i--){
    const d=new Date();d.setDate(d.getDate()-i);
    days.push(d.toLocaleDateString('en-IN',{day:'numeric',month:'short'}));
    let dayRev=0;
    allPayments.forEach(p=>{
      if((p.status==='Success'||p.status==='success')&&p.createdAt){
        const pd=new Date(p.createdAt.seconds*1000);
        if(pd.toDateString()===d.toDateString())dayRev+=p.amount||0;
      }
    });
    revData.push(dayRev);
  }
  const ctx1=document.getElementById(revenueCanvasId);
  if(ctx1){
    // Destroy existing chart on this canvas
    if(revenueCanvasId==='revenueChart'&&revenueChart)revenueChart.destroy();
    if(revenueCanvasId==='revenueChart2'&&revenueChart2)revenueChart2.destroy();
    const chart=new Chart(ctx1,{type:'bar',data:{labels:days,datasets:[{label:'Revenue ₹',data:revData,backgroundColor:'rgba(255,30,30,0.6)',borderRadius:8,borderSkipped:false}]},options:{responsive:true,plugins:{legend:{display:false}},scales:{x:{ticks:{color:'#666'},grid:{display:false}},y:{ticks:{color:'#666',callback:v=>'₹'+v},grid:{color:'#1a1a20'}}}}});
    if(revenueCanvasId==='revenueChart')revenueChart=chart;
    else revenueChart2=chart;
  }
  // Plan distribution pie
  const plans={};
  allUsers.forEach(u=>{const p=(u.subscription||{}).plan||'none';plans[p]=(plans[p]||0)+1});
  const ctx2=document.getElementById(planCanvasId);
  if(ctx2){
    if(planCanvasId==='planChart'&&usersChart)usersChart.destroy();
    if(planCanvasId==='planChart2'&&planChart2)planChart2.destroy();
    const chart=new Chart(ctx2,{type:'doughnut',data:{labels:Object.keys(plans),datasets:[{data:Object.values(plans),backgroundColor:['#ff1e1e','#00e676','#448aff','#ffc107','#b388ff','#666']}]},options:{responsive:true,plugins:{legend:{position:'bottom',labels:{color:'#888',padding:12}}}}});
    if(planCanvasId==='planChart')usersChart=chart;
    else planChart2=chart;
  }
}

// ─── ANALYTICS ───
function renderAnalytics(){
  // Render charts for analytics section
  renderCharts('revenueChart2','planChart2');

  // Calculate analytics stats
  const successPayments=allPayments.filter(p=>p.status==='Success'||p.status==='success');
  const totalRev=successPayments.reduce((sum,p)=>sum+(p.amount||0),0);
  const avgPerDay=Math.round(totalRev/7);

  document.getElementById('avgRevDay').textContent='₹'+avgPerDay.toLocaleString('en-IN');
  document.getElementById('totalPayCount').textContent=allPayments.length;

  const successRate=allPayments.length?Math.round(successPayments.length/allPayments.length*100):0;
  document.getElementById('successRate').textContent=successRate+'%';

  // Most popular plan
  const planCounts={};
  successPayments.forEach(p=>{const plan=p.plan||'unknown';planCounts[plan]=(planCounts[plan]||0)+1});
  let topPlan='—',topCount=0;
  Object.entries(planCounts).forEach(([plan,count])=>{if(count>topCount){topCount=count;topPlan=plan}});
  const planNames={'7days':'7 Days','monthly':'1 Month','3months':'3 Months'};
  document.getElementById('topPlan').textContent=planNames[topPlan]||topPlan;
}

// ─── USERS ───
function renderUsers(){
  try{
    let html='',subs=[];
    allUsers.forEach(u=>{
      const sub=u.subscription||{};
      const isActive=sub.status==='active'&&sub.endDate>Date.now();
      const endStr=sub.endDate?new Date(sub.endDate).toLocaleDateString('en-IN'):'—';
      html+=`<tr><td title="${u.id}">${u.id.substring(0,14)}..</td><td>${u.email||'—'}</td><td>${sub.plan||'none'}</td><td><span class="badge ${isActive?'active':'inactive'}">${isActive?'Active':'Inactive'}</span></td><td>${endStr}</td><td><button class="btn-action green" onclick="openActivateModal('${u.id}','${u.email||''}')">✅ Activate</button><button class="btn-action" onclick="deactivateSub('${u.id}')">❌</button><button class="btn-action blue" onclick="viewUser('${u.id}')">👁</button></td></tr>`;
      if(isActive)subs.push({id:u.id,email:u.email,plan:sub.plan,start:sub.startDate,end:sub.endDate});
    });
    document.getElementById('usersBody').innerHTML=html||'<tr><td colspan="6" class="empty">No users</td></tr>';
    document.getElementById('userCount').textContent=`(${allUsers.length})`;
    // Subscriptions
    let subHtml='';
    subs.forEach(s=>{
      subHtml+=`<tr><td>${s.id.substring(0,14)}..</td><td>${s.email||'—'}</td><td>${s.plan}</td><td>${new Date(s.start).toLocaleDateString('en-IN')}</td><td>${new Date(s.end).toLocaleDateString('en-IN')}</td><td><button class="btn-action" onclick="extendSub('${s.id}')">⏳ Extend</button><button class="btn-action" onclick="deactivateSub('${s.id}')">❌ Cancel</button></td></tr>`;
    });
    document.getElementById('subsBody').innerHTML=subHtml||'<tr><td colspan="6" class="empty">No active subscriptions</td></tr>';
    document.getElementById('subCount').textContent=`(${subs.length})`;
  }catch(e){console.error('Users error:',e)}
}

// ─── PAYMENTS ───
function renderPayments(){
  try{
    let html='';
    allPayments.forEach(p=>{
      const date=p.createdAt?new Date(p.createdAt.seconds*1000).toLocaleString('en-IN',{dateStyle:'short',timeStyle:'short'}):'—';
      const sc=p.status==='pending'?'pending':(p.status==='Success'||p.status==='success')?'success':'failed';
      html+=`<tr><td>${p.id}</td><td title="${p.userId}">${(p.userId||'').substring(0,14)}..</td><td>${p.plan||'—'}</td><td>₹${p.amount||0}</td><td><span class="badge ${sc}">${p.status||'—'}</span></td><td>${date}</td><td>${p.transactionId||'—'}</td></tr>`;
    });
    document.getElementById('paymentsBody').innerHTML=html||'<tr><td colspan="7" class="empty">No payments</td></tr>';
    document.getElementById('payCount').textContent=`(${allPayments.length})`;
  }catch(e){console.error('Payments error:',e)}
}

// ─── ACTIONS ───
function openActivateModal(uid,email){
  document.getElementById('modalUserId').value=uid;
  document.getElementById('modalUserEmail').textContent=email||uid.substring(0,16);
  document.getElementById('activateModal').classList.add('show');
}
function closeModal(){document.querySelectorAll('.modal-overlay').forEach(m=>m.classList.remove('show'))}
async function confirmActivate(){
  const uid=document.getElementById('modalUserId').value;
  const plan=document.getElementById('modalPlan').value;
  const daysMap={'7days':7,'monthly':30,'3months':90};
  const now=Date.now();
  await db.collection('users').doc(uid).update({
    'subscription.plan':plan,'subscription.startDate':now,
    'subscription.endDate':now+(daysMap[plan]*86400000),'subscription.status':'active'
  });
  closeModal();showToast('✅ Subscription activated!');
  allUsers=[];allPayments=[];await refreshAll();
}
async function deactivateSub(uid){
  if(!confirm('Deactivate this subscription?'))return;
  await db.collection('users').doc(uid).update({'subscription.status':'inactive'});
  showToast('Subscription deactivated');allUsers=[];allPayments=[];await refreshAll();
}
async function extendSub(uid){
  const days=prompt('Kitne din extend karna hai?','30');
  if(!days||isNaN(days))return;
  const user=allUsers.find(u=>u.id===uid);
  const currentEnd=(user?.subscription?.endDate)||Date.now();
  const newEnd=currentEnd+(parseInt(days)*86400000);
  await db.collection('users').doc(uid).update({'subscription.endDate':newEnd});
  showToast(`✅ Extended by ${days} days`);allUsers=[];allPayments=[];await refreshAll();
}
function viewUser(uid){
  const u=allUsers.find(x=>x.id===uid);if(!u)return;
  const sub=u.subscription||{};
  let info=`📧 Email: ${u.email||'—'}\n🆔 UID: ${uid}\n📱 Phone: ${u.phone||'—'}\n\n👑 Plan: ${sub.plan||'none'}\n📌 Status: ${sub.status||'inactive'}`;
  if(sub.startDate)info+=`\n📅 Start: ${new Date(sub.startDate).toLocaleDateString('en-IN')}`;
  if(sub.endDate)info+=`\n📅 End: ${new Date(sub.endDate).toLocaleDateString('en-IN')}`;
  alert(info);
}

// ─── EXPORT CSV ───
function exportCSV(type){
  let csv='',rows=[];
  if(type==='users'){
    csv='UID,Email,Plan,Status,EndDate\n';
    allUsers.forEach(u=>{const s=u.subscription||{};csv+=`${u.id},${u.email||''},${s.plan||'none'},${s.status||'inactive'},${s.endDate?new Date(s.endDate).toLocaleDateString():''}\n`});
  }else if(type==='payments'){
    csv='OrderID,UserID,Plan,Amount,Status,Date,TxnID\n';
    allPayments.forEach(p=>{csv+=`${p.id},${p.userId||''},${p.plan||''},${p.amount||0},${p.status||''},${p.createdAt?new Date(p.createdAt.seconds*1000).toLocaleDateString():''},${p.transactionId||''}\n`});
  }
  const blob=new Blob([csv],{type:'text/csv'});
  const a=document.createElement('a');a.href=URL.createObjectURL(blob);
  a.download=`sensixpert_${type}_${new Date().toISOString().split('T')[0]}.csv`;a.click();
  showToast('📥 CSV exported!');
}

// ─── SEARCH ───
function filterTable(id,q){
  const rows=document.getElementById(id).querySelectorAll('tbody tr');
  rows.forEach(r=>{r.style.display=r.textContent.toLowerCase().includes(q.toLowerCase())?'':'none'});
}

// ─── TOAST ───
function showToast(msg){
  const t=document.createElement('div');t.className='toast';t.textContent=msg;
  document.body.appendChild(t);setTimeout(()=>t.remove(),3000);
}

// ─── NOTIFICATIONS ───
function toggleUserSelect(){document.getElementById('specificUserGroup').style.display=document.getElementById('notifTarget').value==='specific'?'block':'none'}
async function sendNotification(){
  const title=document.getElementById('notifTitle').value;
  const msg=document.getElementById('notifMessage').value;
  const target=document.getElementById('notifTarget').value;
  if(!title||!msg){showToast('❌ Title and message required');return}
  const specificUser=target==='specific'?document.getElementById('notifSpecificUser').value:'';
  if(target==='specific'&&!specificUser){showToast('❌ Enter user email or UID');return}

  showToast('📤 Sending notification...');

  try{
    // Build OneSignal API payload
    const payload={
      app_id:ONESIGNAL_APP_ID,
      headings:{en:title},
      contents:{en:msg},
      target_channel:'push'
    };

    // Targeting logic
    if(target==='all'){
      payload.included_segments=['All'];
    }else if(target==='subscribers'){
      payload.included_segments=['All'];
      payload.filters=[{field:'tag',key:'subscribed',value:'true'}];
    }else if(target==='non_subscribers'){
      payload.included_segments=['All'];
      payload.filters=[{field:'tag',key:'subscribed',value:'false'}];
    }else if(target==='specific'&&specificUser){
      payload.include_aliases={external_id:[specificUser]};
      payload.target_channel='push';
    }

    // Call OneSignal REST API
    const response=await fetch('https://api.onesignal.com/notifications',{
      method:'POST',
      headers:{
        'Content-Type':'application/json; charset=utf-8',
        'Authorization':'Key '+ONESIGNAL_API_KEY
      },
      body:JSON.stringify(payload)
    });
    const result=await response.json();
    console.log('OneSignal response:',result);

    // Save to Firestore for history
    await db.collection('notifications').add({
      title,message:msg,target,specificUser:specificUser||null,
      onesignalId:result.id||null,recipients:result.recipients||0,
      createdAt:firebase.firestore.FieldValue.serverTimestamp()
    });

    document.getElementById('notifTitle').value='';
    document.getElementById('notifMessage').value='';

    if(result.id){
      showToast(`📢 Notification sent! (${result.recipients||0} recipients)`);
    }else if(result.errors){
      showToast('❌ '+JSON.stringify(result.errors));
    }else{
      showToast('⚠️ Sent but no recipients found. Users need to open the app first.');
    }
    loadNotifications();
  }catch(e){
    console.error('Send notification error:',e);
    showToast('❌ Error: '+e.message);
  }
}
async function loadNotifications(){
  try{const snap=await db.collection('notifications').orderBy('createdAt','desc').limit(20).get();
  let html='';snap.forEach(d=>{const n=d.data();const date=n.createdAt?new Date(n.createdAt.seconds*1000).toLocaleString('en-IN',{dateStyle:'short',timeStyle:'short'}):'—';
  const targetLabels={'all':'📢 All Users','non_subscribers':'🔓 Non-Subscribers','subscribers':'👑 Subscribers','specific':'🎯 '+(n.specificUser||'').substring(0,10)};
  const tgt=targetLabels[n.target]||n.target;
  html+=`<tr><td>${n.title}</td><td>${n.message.substring(0,40)}${n.message.length>40?'...':''}</td><td>${tgt}</td><td>${date}</td><td><button class="btn-action" onclick="deleteNotif('${d.id}')">🗑️</button></td></tr>`});
  document.getElementById('notifBody').innerHTML=html||'<tr><td colspan="5" class="empty">No notifications sent</td></tr>'}catch(e){console.error(e)}
}
async function deleteNotif(id){if(!confirm('Delete this notification?'))return;await db.collection('notifications').doc(id).delete();showToast('🗑️ Notification deleted');loadNotifications()}

// ─── PRICING ───
async function loadPricing(){
  try{const doc=await db.collection('config').doc('pricing').get();
  if(doc.exists){const d=doc.data();
  if(d['7days']){document.getElementById('price7days').value=d['7days'].price||49;document.getElementById('days7days').value=d['7days'].days||7}
  if(d['monthly']){document.getElementById('priceMonthly').value=d['monthly'].price||169;document.getElementById('daysMonthly').value=d['monthly'].days||30}
  if(d['3months']){document.getElementById('price3months').value=d['3months'].price||399;document.getElementById('days3months').value=d['3months'].days||90}
  }}catch(e){console.error(e)}
}
async function savePricing(){
  const pricing={
    '7days':{price:parseInt(document.getElementById('price7days').value),days:parseInt(document.getElementById('days7days').value)},
    'monthly':{price:parseInt(document.getElementById('priceMonthly').value),days:parseInt(document.getElementById('daysMonthly').value)},
    '3months':{price:parseInt(document.getElementById('price3months').value),days:parseInt(document.getElementById('days3months').value)},
    updatedAt:firebase.firestore.FieldValue.serverTimestamp()
  };
  // Validate
  for(const[key,val] of Object.entries(pricing)){
    if(key==='updatedAt')continue;
    if(!val.price||val.price<=0){showToast('❌ Invalid price for '+key);return}
    if(!val.days||val.days<=0){showToast('❌ Invalid days for '+key);return}
  }
  await db.collection('config').doc('pricing').set(pricing);
  showToast('💰 Pricing saved! Note: Update backend PLANS config to sync.');
}

// ─── POLICIES ───
async function loadPolicies(){
  try{const doc=await db.collection('config').doc('policies').get();
  if(doc.exists){const d=doc.data();
  if(d.privacy)document.getElementById('privacyText').value=d.privacy;
  if(d.terms)document.getElementById('termsText').value=d.terms;
  }}catch(e){console.error(e)}
}
async function savePolicy(type){
  const text=document.getElementById(type==='privacy'?'privacyText':'termsText').value;
  if(!text){showToast('❌ Enter content');return}
  const update={};update[type]=text;update[type+'UpdatedAt']=firebase.firestore.FieldValue.serverTimestamp();
  await db.collection('config').doc('policies').set(update,{merge:true});
  showToast('📜 '+type.charAt(0).toUpperCase()+type.slice(1)+' saved!');
}

// Auto-refresh every 60s
setInterval(()=>{allUsers=[];allPayments=[];refreshAll()},60000);
