const STORAGE='mobilestore-v1';
const initialProducts=[
 {id:'iphone-15',brand:'Apple',name:'iPhone 15',price:3499,stock:3,color:'apple'},
 {id:'iphone-13',brand:'Apple',name:'iPhone 13',price:2499,stock:5,color:'apple'},
 {id:'s24-ultra',brand:'Samsung',name:'Galaxy S24 Ultra',price:4599,stock:4,color:'samsung'},
 {id:'a55',brand:'Samsung',name:'Galaxy A55 5G',price:1399,stock:8,color:'samsung'},
 {id:'xiaomi-14',brand:'Xiaomi',name:'Xiaomi 14',price:2399,stock:6,color:'xiaomi'},
 {id:'redmi-note-13',brand:'Xiaomi',name:'Redmi Note 13 Pro',price:999,stock:10,color:'xiaomi'}
];
let state=load(),activeBrand='Todas';
function load(){try{return JSON.parse(localStorage.getItem(STORAGE))||{products:initialProducts,clients:[],sales:[],cart:[]}}catch{return{products:initialProducts,clients:[],sales:[],cart:[]}}}
function save(){localStorage.setItem(STORAGE,JSON.stringify(state))}
function money(c){return `S/ ${(c/100).toLocaleString('es-PE',{minimumFractionDigits:2,maximumFractionDigits:2})}`}
function cents(n){return Math.round(n*100)}
function esc(v){return String(v).replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]))}
function renderProducts(){
 const term=document.querySelector('#searchInput').value.toLowerCase(), ps=state.products.filter(p=>(activeBrand==='Todas'||p.brand===activeBrand)&&(`${p.brand} ${p.name}`).toLowerCase().includes(term));
 document.querySelector('#productResult').textContent=`${ps.length} productos`;
 document.querySelector('#productGrid').innerHTML=ps.length?ps.map(p=>`<article class="product-card"><div class="product-visual ${p.color}"><span class="stock ${p.stock===0?'empty':p.stock<3?'low':''}">${p.stock===0?'Agotado':`${p.stock} disponibles`}</span><div class="mini-phone"></div></div><span class="brand-label">${esc(p.brand)}</span><h3>${esc(p.name)}</h3><div class="card-footer"><span class="price">${money(cents(p.price))}</span><button class="add" data-add="${p.id}" ${p.stock===0?'disabled':''}>Añadir</button></div></article>`).join(''):'<div class="empty" style="grid-column:1/-1">No encontramos productos con esos filtros.</div>';
}
function totals(){const subtotal=state.cart.reduce((s,i)=>s+cents(state.products.find(p=>p.id===i.id).price)*i.qty,0),discount=state.cart.reduce((s,i)=>i.qty>=2?s+Math.round(cents(state.products.find(p=>p.id===i.id).price)*i.qty*.05):s,0),tax=Math.round((subtotal-discount)*.18);return{subtotal,discount,tax,total:subtotal-discount+tax}}
function renderCart(){
 document.querySelector('#cartCount').textContent=state.cart.reduce((s,i)=>s+i.qty,0);
 document.querySelector('#cartItems').innerHTML=state.cart.length?state.cart.map(i=>{const p=state.products.find(x=>x.id===i.id);return`<div class="cart-line"><div class="cart-thumb ${p.color}"><div class="mini-phone"></div></div><div><h4>${esc(p.name)}</h4><small>${money(cents(p.price))} c/u</small><div class="qty"><button data-minus="${p.id}">−</button><b>${i.qty}</b><button data-plus="${p.id}">+</button></div><button class="remove" data-remove="${p.id}">Eliminar</button></div><span class="line-price">${money(cents(p.price)*i.qty)}</span></div>`}).join(''):'<div class="empty">Tu carrito está vacío.<br>Añade productos para comenzar.</div>';
 const t=totals();['subtotal','discount','tax','total'].forEach(k=>document.querySelector('#'+k).textContent=(k==='discount'?'− ':'')+money(t[k]));
}
function renderClients(){
 document.querySelector('#clientList').innerHTML=state.clients.length?state.clients.slice().reverse().map(c=>`<div class="client-row"><div><b>${esc(c.name)}</b><small>${esc(c.document)}${c.email?' · '+esc(c.email):''}</small></div><button class="delete-client" data-client="${c.id}">Eliminar</button></div>`).join(''):'<div class="empty">No hay clientes registrados.</div>';
 document.querySelector('#customerSelect').innerHTML='<option value="">Venta mostrador</option>'+state.clients.map(c=>`<option value="${c.id}">${esc(c.name)} — ${esc(c.document)}</option>`).join('');
}
function renderSales(){document.querySelector('#saleCount').textContent=`${state.sales.length} ${state.sales.length===1?'venta':'ventas'}`;document.querySelector('#salesList').innerHTML=state.sales.length?state.sales.slice().reverse().slice(0,8).map(s=>`<div class="sale-row"><div><b>${s.id}</b><small>${new Date(s.date).toLocaleString('es-PE')} · ${esc(s.customer||'Venta mostrador')} · ${s.items.length} producto(s)</small></div><strong>${money(s.total)}</strong></div>`).join(''):'<div class="empty">Aún no hay ventas registradas.</div>'}
function toast(msg){const e=document.querySelector('#toast');e.textContent=msg;e.classList.add('show');setTimeout(()=>e.classList.remove('show'),2800)}
function add(id){const p=state.products.find(x=>x.id===id),item=state.cart.find(x=>x.id===id);if(!p||((item?.qty||0)+1)>p.stock)return toast('No hay más unidades disponibles.');if(item)item.qty++;else state.cart.push({id,qty:1});save();renderCart();toast(`${p.name} añadido al carrito.`)}
function qty(id,d){const item=state.cart.find(x=>x.id===id),p=state.products.find(x=>x.id===id);if(!item)return;if(item.qty+d>p.stock)return toast('La cantidad supera el stock disponible.');item.qty+=d;if(item.qty<1)state.cart=state.cart.filter(x=>x.id!==id);save();renderCart()}
function closeDrawer(){document.querySelector('#cartDrawer').classList.remove('open');document.querySelector('#drawerBackdrop').classList.remove('open')}
function checkout(){
 if(!state.cart.length)return toast('El carrito está vacío.');
 // Se valida todo antes de modificar inventario: nunca hay ventas parciales.
 for(const i of state.cart){const p=state.products.find(x=>x.id===i.id);if(!p||i.qty<1||i.qty>p.stock)return toast(`Stock insuficiente para ${p?.name||'un producto'}.`)}
 const t=totals(),c=state.clients.find(x=>x.id===document.querySelector('#customerSelect').value);
 const sale={id:`V-${String(state.sales.length+1).padStart(4,'0')}`,date:new Date().toISOString(),customer:c?.name||'Venta mostrador',clientId:c?.id||null,items:state.cart.map(i=>{const p=state.products.find(x=>x.id===i.id);return{productId:p.id,name:p.name,quantity:i.qty,unitPrice:cents(p.price),subtotal:cents(p.price)*i.qty}}),subtotal:t.subtotal,discount:t.discount,tax:t.tax,total:t.total};
 state.cart.forEach(i=>state.products.find(p=>p.id===i.id).stock-=i.qty);state.sales.push(sale);state.cart=[];save();renderProducts();renderCart();renderSales();closeDrawer();toast(`Venta ${sale.id} registrada correctamente.`)
}
document.addEventListener('click',e=>{const a=e.target.closest('[data-add]');if(a)add(a.dataset.add);const plus=e.target.closest('[data-plus]');if(plus)qty(plus.dataset.plus,1);const minus=e.target.closest('[data-minus]');if(minus)qty(minus.dataset.minus,-1);const rem=e.target.closest('[data-remove]');if(rem){state.cart=state.cart.filter(i=>i.id!==rem.dataset.remove);save();renderCart()}const del=e.target.closest('[data-client]');if(del){state.clients=state.clients.filter(c=>c.id!==del.dataset.client);save();renderClients()}const f=e.target.closest('[data-brand]');if(f){activeBrand=f.dataset.brand;document.querySelectorAll('.filter').forEach(x=>x.classList.toggle('active',x===f));renderProducts()}});
document.querySelector('#searchInput').addEventListener('input',renderProducts);document.querySelector('#cartButton').addEventListener('click',()=>{document.querySelector('#cartDrawer').classList.add('open');document.querySelector('#drawerBackdrop').classList.add('open')});document.querySelector('#closeCart').addEventListener('click',closeDrawer);document.querySelector('#drawerBackdrop').addEventListener('click',closeDrawer);document.querySelector('#checkoutButton').addEventListener('click',checkout);document.querySelector('#clientForm').addEventListener('submit',e=>{e.preventDefault();const name=document.querySelector('#clientName').value.trim(),documentId=document.querySelector('#clientDocument').value.trim(),email=document.querySelector('#clientEmail').value.trim();state.clients.push({id:crypto.randomUUID?crypto.randomUUID():Date.now().toString(),name,document:documentId,email});save();renderClients();e.target.reset();toast('Cliente registrado correctamente.')});document.querySelector('#newClientButton').addEventListener('click',()=>document.querySelector('#clientName').focus());
renderProducts();renderCart();renderClients();renderSales();
